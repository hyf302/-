// 搜索功能
function searchScenicSpots() {
    const keyword = document.querySelector('input[name="keyword"]').value;
    window.location.href = '/admin/scenic-spots' + (keyword ? '?keyword=' + encodeURIComponent(keyword) : '');
}

// 添加回车搜索功能
document.querySelector('input[name="keyword"]').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        searchScenicSpots();
    }
});

// 提交添加景点表单
function submitAddScenicSpot() {
    const form = document.getElementById('addScenicSpotForm');
    const formData = new FormData(form);
    const data = Object.fromEntries(formData);
    
    fetch('/admin/api/scenic-spots', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        if (result.code === 200) {
            alert('添加成功');
            window.location.reload();
        } else {
            alert('添加失败：' + result.message);
        }
    })
    .catch(error => {
        alert('添加失败：' + error);
    });
}

// 编辑景点
function editScenicSpot(id) {
    fetch(`/admin/api/scenic-spots/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                const spot = result.data;
                // 填充表单
                document.querySelector('[name="name"]').value = spot.name;
                document.querySelector('[name="address"]').value = spot.address;
                document.querySelector('[name="openTime"]').value = spot.openTime;
                document.querySelector('[name="closeTime"]').value = spot.closeTime;
                document.querySelector('[name="price"]').value = spot.price;
                document.querySelector('[name="maxCapacity"]').value = spot.maxCapacity;
                
                // 显示编辑模态框
                const modal = new bootstrap.Modal(document.getElementById('addScenicSpotModal'));
                modal.show();
                
                // 修改提交按钮的行为
                const submitBtn = document.querySelector('#addScenicSpotModal .btn-primary');
                submitBtn.textContent = '保存';
                submitBtn.onclick = () => updateScenicSpot(id);
            } else {
                alert('获取景点信息失败：' + result.message);
            }
        });
}

// 更新景点信息
function updateScenicSpot(id) {
    const form = document.getElementById('addScenicSpotForm');
    const formData = new FormData(form);
    const data = Object.fromEntries(formData);
    
    fetch(`/admin/api/scenic-spots/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        if (result.code === 200) {
            alert('更新成功');
            window.location.reload();
        } else {
            alert('更新失败：' + result.message);
        }
    })
    .catch(error => {
        alert('更新失败：' + error);
    });
}

// 删除景点
function deleteScenicSpot(id) {
    if (confirm('确定要删除这个景点吗？')) {
        fetch(`/admin/api/scenic-spots/${id}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                alert('删除成功');
                window.location.reload();
            } else {
                alert('删除失败：' + result.message);
            }
        })
        .catch(error => {
            alert('删除失败：' + error);
        });
    }
}

// 更新景点状态
function updateStatus(id, status) {
    $.ajax({
        url: `/admin/api/scenic-spots/${id}/status`,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({ status: status }),
        success: function(response) {
            if (response.code === 200) {
                window.location.reload();
            } else {
                alert('更新失败：' + response.message);
            }
        },
        error: function() {
            alert('更新失败，请稍后重试');
        }
    });
}

// 图片预览功能
function previewImages(input, previewContainer) {
    if (input.files) {
        $(previewContainer).empty();
        
        const files = Array.from(input.files);
        files.forEach(file => {
            const reader = new FileReader();
            reader.onload = function(e) {
                const div = document.createElement('div');
                div.className = 'image-preview-item';
                div.innerHTML = `
                    <img src="${e.target.result}">
                    <span class="delete-image">&times;</span>
                `;
                $(previewContainer).append(div);
            }
            reader.readAsDataURL(file);
        });
    }
}

// 图片上传函数
async function uploadImages(files) {
    const formData = new FormData();
    Array.from(files).forEach(file => {
        formData.append('files', file);
    });
    
    try {
        const response = await fetch('/admin/api/upload', {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            throw new Error('上传失败');
        }
        
        const result = await response.json();
        if (result.code === 200) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('上传图片失败:', error);
        throw error;
    }
}

// 显示已有图片预览
function showExistingImages(imageUrls) {
    const previewContainer = document.getElementById('editImagePreview');
    previewContainer.innerHTML = '';
    
    if (imageUrls) {
        imageUrls.split(',').forEach(url => {
            const div = document.createElement('div');
            div.className = 'image-preview-item';
            div.innerHTML = `
                <img src="${url}" alt="景点图片">
                <span class="delete-image" data-url="${url}">&times;</span>
            `;
            previewContainer.appendChild(div);
        });
    }
}

// 删除预览图片
$(document).on('click', '.delete-image', function() {
    const url = $(this).data('url');
    const form = $(this).closest('form');
    const imageUrls = form.find('input[name="imageUrls"]').val();
    
    if (url && imageUrls) {
        const urls = imageUrls.split(',').filter(u => u !== url);
        form.find('input[name="imageUrls"]').val(urls.join(','));
    }
    
    $(this).closest('.image-preview-item').remove();
});

// 修改添加景点的提交处理
$('#submitAdd').click(async function() {
    try {
        const form = $('#addScenicSpotForm')[0];
        const formData = new FormData(form);
        
        // 上传图片
        const imageFiles = form.querySelector('input[name="imageFile"]').files;
        if (imageFiles.length > 0) {
            const imageUrls = await uploadImages(imageFiles);
            formData.set('imageUrls', imageUrls.join(','));
        }
        
        // 提交景点数据
        const response = await fetch('/admin/api/scenic-spots', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(Object.fromEntries(formData))
        });
        
        const result = await response.json();
        if (result.code === 200) {
            location.reload();
        } else {
            alert(result.message);
        }
    } catch (error) {
        console.error('添加景点失败:', error);
        alert('添加景点失败');
    }
});

// 为文件输入框添加预览监听
$('input[name="imageFile"]').change(function() {
    const previewContainer = $(this).closest('form').find('.image-preview-item').parent();
    previewImages(this, previewContainer);
});

$(document).ready(function() {
    // 添加景点
    $('#submitAdd').click(function() {
        const formData = {};
        $('#addScenicSpotForm').serializeArray().forEach(item => {
            formData[item.name] = item.value;
        });
        
        $.ajax({
            url: '/admin/api/scenic-spots',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                if (response.code === 200) {
                    alert('添加成功');
                    location.reload();
                } else {
                    alert(response.message || '添加失败');
                }
            },
            error: function() {
                alert('系统错误');
            }
        });
    });

    // 编辑景点
    $('.edit-btn').click(function() {
        const id = $(this).data('id');
        $.get(`/admin/api/scenic-spots/${id}`, function(response) {
            if (response.code === 200) {
                const spot = response.data;
                const form = $('#editScenicSpotForm');
                
                // 填充表单数据
                form.find('[name=id]').val(spot.id);
                form.find('[name=name]').val(spot.name);
                form.find('[name=description]').val(spot.description);
                form.find('[name=address]').val(spot.address);
                form.find('[name=openTime]').val(spot.openTime);
                form.find('[name=closeTime]').val(spot.closeTime);
                form.find('[name=maxCapacity]').val(spot.maxCapacity);
                form.find('[name=price]').val(spot.price);
                form.find('[name=imageUrls]').val(spot.imageUrls);
                
                // 显示已有图片预览
                showExistingImages(spot.imageUrls);
                
                // 显示模态框
                $('#editScenicSpotModal').modal('show');
            } else {
                alert('获取景点信息失败');
            }
        });
    });

    // 修改编辑景点的提交处理
    $('#editScenicSpotModal .btn-primary').click(async function() {
        try {
            const form = document.getElementById('editScenicSpotForm');
            const id = form.querySelector('[name=id]').value;
            const formData = new FormData(form);
            
            // 上传图片
            const imageFiles = form.querySelector('input[name="imageFile"]').files;
            if (imageFiles.length > 0) {
                const imageUrls = await uploadImages(imageFiles);
                // 合并新旧图片URL
                const existingUrls = form.querySelector('input[name="imageUrls"]').value;
                const allUrls = existingUrls ? 
                    existingUrls.split(',').concat(imageUrls) : 
                    imageUrls;
                formData.set('imageUrls', allUrls.join(','));
            }
            
            // 构建提交数据
            const data = {
                id: formData.get('id'),
                name: formData.get('name'),
                description: formData.get('description'),
                address: formData.get('address'),
                openTime: formData.get('openTime'),
                closeTime: formData.get('closeTime'),
                maxCapacity: parseInt(formData.get('maxCapacity')),
                price: parseFloat(formData.get('price')),
                imageUrls: formData.get('imageUrls'),
                status: parseInt(formData.get('status') || '1')
            };

            const response = await fetch(`/admin/api/scenic-spots/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            const result = await response.json();
            if (result.code === 200) {
                alert('更新成功');
                location.reload();
            } else {
                alert(result.message || '更新失败');
            }
        } catch (error) {
            console.error('更新景点失败:', error);
            alert('更新失败');
        }
    });

    // 删除景点
    $('.delete-btn').click(function() {
        if (!confirm('确定要删除这个景点吗？')) {
            return;
        }
        
        const id = $(this).data('id');
        $.ajax({
            url: '/admin/api/scenic-spots/' + id,
            type: 'DELETE',
            success: function(response) {
                if (response.code === 200) {
                    alert('删除成功');
                    location.reload();
                } else {
                    alert(response.message || '删除失败');
                }
            },
            error: function() {
                alert('系统错误');
            }
        });
    });

    // 切换景点状态
    $('.toggle-status').click(function() {
        const id = $(this).data('id');
        const currentStatus = $(this).data('status');
        const newStatus = currentStatus === 1 ? 0 : 1;
        
        $.ajax({
            url: `/admin/api/scenic-spots/${id}/status`,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ status: newStatus }),
            success: function(response) {
                if (response.code === 200) {
                    location.reload();
                } else {
                    alert(response.message || '操作失败');
                }
            },
            error: function() {
                alert('系统错误');
            }
        });
    });
});

function showImages(spot) {
    const images = spot.imageUrls ? spot.imageUrls.split(',') : [];
    let imageHtml = '';
    
    if (images.length > 0) {
        imageHtml = `<img src="${images[0]}" class="card-img-top" alt="${spot.name}">`;
    } else {
        imageHtml = '<img src="/images/default-scenic.jpg" class="card-img-top" alt="默认图片">';
    }
    
    return imageHtml;
} 