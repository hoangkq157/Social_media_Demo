/**
 * SpringBook - Client Javascript (Vanilla JS)
 * Xu ly AJAX Like, Comment, Modal Composer Media Preview, Load More, Flash Alert
 */
document.addEventListener('DOMContentLoaded', function () {
  // 1. Doc CSRF Token tu the meta trong layout
  const csrfTokenEl = document.querySelector('meta[name="_csrf"]');
  const csrfHeaderEl = document.querySelector('meta[name="_csrf_header"]');
  const csrfToken = csrfTokenEl ? csrfTokenEl.getAttribute('content') : '';
  const csrfHeader = csrfHeaderEl ? csrfHeaderEl.getAttribute('content') : 'X-CSRF-TOKEN';

  function getHeaders(isJson = false) {
    const headers = {
      'X-Requested-With': 'XMLHttpRequest'
    };
    if (csrfToken) {
      headers[csrfHeader] = csrfToken;
    }
    if (isJson) {
      headers['Content-Type'] = 'application/json';
    }
    return headers;
  }

  // 2. Tu dong an flash alert sau 4 giay
  const flashAlerts = document.querySelectorAll('.fb-flash-alert');
  flashAlerts.forEach(function (alert) {
    setTimeout(function () {
      alert.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
      alert.style.opacity = '0';
      alert.style.transform = 'translateY(10px)';
      setTimeout(function () {
        alert.remove();
      }, 500);
    }, 4000);
  });

  // 3. Modal Composer: Nhap noi dung & Dinh kem Media
  const postContentInput = document.getElementById('postContentInput');
  const mediaInput = document.getElementById('mediaInput');
  const mediaPreviewContainer = document.getElementById('mediaPreviewContainer');
  const btnRemoveMedia = document.getElementById('btnRemoveMedia');
  const previewMediaElement = document.getElementById('previewMediaElement');
  const btnSubmitPost = document.getElementById('btnSubmitPost');
  const postComposerForm = document.getElementById('postComposerForm');
  const btnQuickUploadMedia = document.getElementById('btnQuickUploadMedia');

  function updateSubmitButtonState() {
    if (!btnSubmitPost) return;
    const hasText = postContentInput && postContentInput.value.trim().length > 0;
    const hasMedia = mediaInput && mediaInput.files && mediaInput.files.length > 0;

    btnSubmitPost.disabled = !(hasText || hasMedia);

    if (postContentInput) {
      if (hasMedia) {
        postContentInput.classList.add('has-media');
      } else {
        postContentInput.classList.remove('has-media');
      }
    }
  }

  if (postContentInput) {
    postContentInput.addEventListener('input', updateSubmitButtonState);
  }

  // Nut nhanh "Anh/video" tren composer card
  if (btnQuickUploadMedia && mediaInput) {
    btnQuickUploadMedia.addEventListener('click', function () {
      const modalEl = document.getElementById('createPostModal');
      if (modalEl && window.bootstrap && window.bootstrap.Modal) {
        const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        modal.show();
      }
      setTimeout(function () {
        mediaInput.click();
      }, 350);
    });
  }

  if (mediaInput && mediaPreviewContainer) {
    mediaInput.addEventListener('change', function () {
      const file = this.files[0];
      if (!file) {
        mediaPreviewContainer.style.display = 'none';
        updateSubmitButtonState();
        return;
      }

      const fileUrl = URL.createObjectURL(file);
      previewMediaElement.innerHTML = '';

      if (file.type.startsWith('image/')) {
        const img = document.createElement('img');
        img.src = fileUrl;
        img.alt = 'Xem trước ảnh';
        previewMediaElement.appendChild(img);
        mediaPreviewContainer.style.display = 'flex';
      } else if (file.type.startsWith('video/')) {
        const video = document.createElement('video');
        video.src = fileUrl;
        video.controls = true;
        previewMediaElement.appendChild(video);
        mediaPreviewContainer.style.display = 'flex';
      } else {
        alert('Chỉ hỗ trợ file ảnh (JPG, PNG, GIF, WEBP) hoặc video (MP4, WEBM)!');
        this.value = '';
        mediaPreviewContainer.style.display = 'none';
      }
      updateSubmitButtonState();
    });

    if (btnRemoveMedia) {
      btnRemoveMedia.addEventListener('click', function () {
        mediaInput.value = '';
        previewMediaElement.innerHTML = '';
        mediaPreviewContainer.style.display = 'none';
        updateSubmitButtonState();
      });
    }
  }

  // Khoa nut submit khi dang gui form tao bai viet de chong bam dup
  if (postComposerForm && btnSubmitPost) {
    postComposerForm.addEventListener('submit', function () {
      btnSubmitPost.disabled = true;
      btnSubmitPost.textContent = 'Đang đăng...';
    });
  }

  // 4. Delegate event cho Like button
  document.body.addEventListener('click', function (e) {
    const likeBtn = e.target.closest('.fb-btn-like');
    if (!likeBtn) return;

    e.preventDefault();
    if (likeBtn.dataset.busy === 'true') return;
    likeBtn.dataset.busy = 'true';

    const postId = likeBtn.dataset.postId;
    const countSpan = document.getElementById('like-count-' + postId);
    const wasLiked = likeBtn.classList.contains('liked');
    const labelSpan = likeBtn.querySelector('.fb-like-label');
    const iconUse = likeBtn.querySelector('use');
    let currentCount = parseInt(countSpan ? countSpan.textContent : '0', 10);

    // Optimistic UI update
    if (wasLiked) {
      likeBtn.classList.remove('liked');
      if (labelSpan) labelSpan.textContent = 'Thích';
      if (iconUse) iconUse.setAttribute('href', '/icons/sprite.svg#like');
      if (countSpan) countSpan.textContent = Math.max(0, currentCount - 1);
    } else {
      likeBtn.classList.add('liked');
      if (labelSpan) labelSpan.textContent = 'Đã thích';
      if (iconUse) iconUse.setAttribute('href', '/icons/sprite.svg#like-fill');
      if (countSpan) countSpan.textContent = currentCount + 1;
    }

    fetch('/api/posts/' + postId + '/like', {
      method: 'POST',
      headers: getHeaders(true)
    })
      .then(function (res) {
        if (!res.ok) throw new Error('Yêu cầu không thành công');
        return res.json();
      })
      .then(function (data) {
        if (data.liked) {
          likeBtn.classList.add('liked');
          if (labelSpan) labelSpan.textContent = 'Đã thích';
          if (iconUse) iconUse.setAttribute('href', '/icons/sprite.svg#like-fill');
        } else {
          likeBtn.classList.remove('liked');
          if (labelSpan) labelSpan.textContent = 'Thích';
          if (iconUse) iconUse.setAttribute('href', '/icons/sprite.svg#like');
        }
        if (countSpan) {
          countSpan.textContent = data.likeCount;
        }
      })
      .catch(function (err) {
        console.error('Lỗi like:', err);
        // Rollback
        if (wasLiked) {
          likeBtn.classList.add('liked');
          if (labelSpan) labelSpan.textContent = 'Đã thích';
          if (iconUse) iconUse.setAttribute('href', '/icons/sprite.svg#like-fill');
        } else {
          likeBtn.classList.remove('liked');
          if (labelSpan) labelSpan.textContent = 'Thích';
          if (iconUse) iconUse.setAttribute('href', '/icons/sprite.svg#like');
        }
        if (countSpan) countSpan.textContent = currentCount;
        alert('Không thể thực hiện thích bài viết. Vui lòng thử lại!');
      })
      .finally(function () {
        likeBtn.dataset.busy = 'false';
      });
  });

  // 5. Delegate event cho submit binh luan (qua Enter hoac form)
  document.body.addEventListener('submit', function (e) {
    const commentForm = e.target.closest('.fb-comment-form');
    if (!commentForm) return;

    e.preventDefault();
    const postId = commentForm.dataset.postId;
    const input = commentForm.querySelector('.fb-comment-input');
    const content = input ? input.value.trim() : '';

    if (!content) return;
    if (commentForm.dataset.busy === 'true') return;
    commentForm.dataset.busy = 'true';
    if (input) input.disabled = true;

    fetch('/api/posts/' + postId + '/comments', {
      method: 'POST',
      headers: getHeaders(true),
      body: JSON.stringify({ content: content })
    })
      .then(function (res) {
        if (!res.ok) throw new Error('Không thể đăng bình luận');
        return res.json();
      })
      .then(function (comment) {
        // Chen binh luan bang DOM methods (an toan XSS)
        const commentsList = document.getElementById('comments-list-' + postId);
        if (commentsList) {
          const commentEl = createCommentElement(comment);
          commentsList.appendChild(commentEl);
        }

        // Cap nhat so luong binh luan
        const commentCountEl = document.getElementById('comment-count-' + postId);
        if (commentCountEl) {
          const cur = parseInt(commentCountEl.textContent, 10) || 0;
          commentCountEl.textContent = cur + 1;
        }

        if (input) {
          input.value = '';
        }
      })
      .catch(function (err) {
        console.error('Lỗi bình luận:', err);
        alert('Lỗi gửi bình luận: ' + err.message);
      })
      .finally(function () {
        commentForm.dataset.busy = 'false';
        if (input) {
          input.disabled = false;
          input.focus();
        }
      });
  });

  // Tao phan tu binh luan moi an toan
  function createCommentElement(c) {
    const item = document.createElement('div');
    item.className = 'fb-comment-row';

    // Avatar
    let avatarEl;
    if (c.authorAvatarPath) {
      avatarEl = document.createElement('img');
      avatarEl.src = c.authorAvatarPath;
      avatarEl.className = 'fb-avatar fb-avatar-sm';
      avatarEl.alt = c.authorFullName;
    } else {
      avatarEl = document.createElement('div');
      avatarEl.className = 'fb-avatar fb-avatar-sm';
      avatarEl.style.backgroundColor = c.authorAvatarColor || '#1877F2';
      avatarEl.textContent = c.authorInitials || '?';
    }
    item.appendChild(avatarEl);

    // Wrap
    const wrap = document.createElement('div');
    wrap.style.flex = '1';

    // Bubble
    const bubble = document.createElement('div');
    bubble.className = 'fb-comment-bubble-v2';

    const authorLink = document.createElement('a');
    authorLink.href = '/users/' + encodeURIComponent(c.authorUsername);
    authorLink.className = 'fb-comment-author-v2';
    authorLink.textContent = c.authorFullName;
    bubble.appendChild(authorLink);

    const textEl = document.createElement('div');
    textEl.className = 'fb-comment-content-v2';
    textEl.textContent = c.content;
    bubble.appendChild(textEl);

    wrap.appendChild(bubble);

    // Time
    const timeEl = document.createElement('div');
    timeEl.className = 'fb-comment-time-v2';
    timeEl.textContent = c.createdAtFormatted || 'Vừa xong';
    wrap.appendChild(timeEl);

    item.appendChild(wrap);
    return item;
  }

  // 6. Nut "Tai them" (Load More) bang tin
  const btnLoadMore = document.getElementById('btnLoadMore');
  const postListContainer = document.getElementById('postListContainer');

  if (btnLoadMore && postListContainer) {
    btnLoadMore.addEventListener('click', function () {
      const nextPage = parseInt(this.dataset.nextPage, 10);
      this.disabled = true;
      const originalText = this.textContent;
      this.textContent = 'Đang tải...';

      fetch('/feed/more?page=' + nextPage, {
        headers: getHeaders(false)
      })
        .then(function (res) {
          if (!res.ok) throw new Error('Không thể tải thêm bài viết');
          return res.text();
        })
        .then(function (html) {
          const parser = new DOMParser();
          const doc = parser.parseFromString(html, 'text/html');
          const newPosts = doc.querySelectorAll('.fb-post-card-item');

          newPosts.forEach(function (post) {
            postListContainer.appendChild(post);
          });

          const hasNextMeta = doc.getElementById('hasNextMarker');
          const hasNext = hasNextMeta ? hasNextMeta.dataset.hasNext === 'true' : false;

          if (hasNext) {
            btnLoadMore.dataset.nextPage = nextPage + 1;
            btnLoadMore.disabled = false;
            btnLoadMore.textContent = originalText;
          } else {
            btnLoadMore.parentElement.remove();
          }
        })
        .catch(function (err) {
          console.error('Lỗi tải thêm:', err);
          btnLoadMore.disabled = false;
          btnLoadMore.textContent = originalText;
          alert('Không thể tải thêm bài viết.');
        });
    });
  }
});
