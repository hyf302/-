// 保存公告
function saveAnnouncement() {
    const title = $('#announcementTitle').val();
    const content = $('#announcementContent').val();
    const isPublish = $('#publishNow').prop('checked');
    
    const data = {
        title: title,
        content: content,
        status: isPublish ? 1 : 0  // 1表示已发布，0表示未发布
    };
    
    // 如果是编辑模式，需要添加id
    if (currentEditId) {
        data.id = currentEditId;
    }
    
    $.ajax({
        url: '/admin/api/announcements',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(response) {
            if (response.code === 200) {
                $('#announcementModal').modal('hide');
                loadAnnouncements(); // 重新加载公告列表
                showToast('success', '保存成功');
            } else {
                showToast('error', response.message || '保存失败');
            }
        },
        error: function() {
            showToast('error', '保存失败，请稍后重试');
        }
    });
}

// 显示添加公告模态框
function showAddModal() {
    currentEditId = null;
    $('#announcementTitle').val('');
    $('#announcementContent').val('');
    $('#publishNow').prop('checked', false);
    $('#announcementModalLabel').text('添加公告');
    $('#announcementModal').modal('show');
}

// 绑定保存按钮点击事件
document.querySelector('#saveAnnouncement').addEventListener('click', saveAnnouncement);

// 查看公告详情
function viewAnnouncement(id) {
    fetch(`/admin/api/announcements/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                // 填充模态框数据
                document.getElementById('title').value = result.data.title;
                document.getElementById('content').value = result.data.content;
                document.getElementById('publish').checked = result.data.status === 1;
                
                // 禁用编辑
                document.getElementById('title').disabled = true;
                document.getElementById('content').disabled = true;
                document.getElementById('publish').disabled = true;
                
                // 隐藏保存按钮
                document.getElementById('saveAnnouncement').style.display = 'none';
                
                // 显示模态框
                $('#announcementModal').modal('show');
            } else {
                alert('获取公告详情失败：' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('获取公告详情失败，请重试');
        });
}

// 编辑公告
function editAnnouncement(id) {
    fetch(`/admin/api/announcements/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                // 填充模态框数据
                document.getElementById('title').value = result.data.title;
                document.getElementById('content').value = result.data.content;
                document.getElementById('publish').checked = result.data.status === 1;
                
                // 启用编辑
                document.getElementById('title').disabled = false;
                document.getElementById('content').disabled = false;
                document.getElementById('publish').disabled = false;
                
                // 显示保存按钮
                document.getElementById('saveAnnouncement').style.display = 'block';
                document.getElementById('saveAnnouncement').onclick = () => updateAnnouncement(id);
                
                // 显示模态框
                $('#announcementModal').modal('show');
            } else {
                alert('获取公告详情失败：' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('获取公告详情失败，请重试');
        });
}

// 更新公告
function updateAnnouncement(id) {
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    const publish = document.getElementById('publish').checked;

    const data = {
        id: id,
        title: title,
        content: content,
        status: publish ? 1 : 0
    };

    fetch(`/admin/api/announcements/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        if (result.code === 200) {
            // 关闭模态框并刷新页面
            $('#announcementModal').modal('hide');
            window.location.reload();
        } else {
            alert('更新失败：' + result.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('更新失败，请重试');
    });
}

// 删除公告
function deleteAnnouncement(id) {
    if (confirm('确定要删除这条公告吗？')) {
        fetch(`/admin/api/announcements/${id}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                window.location.reload();
            } else {
                alert('删除失败：' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('删除失败，请重试');
        });
    }
} 