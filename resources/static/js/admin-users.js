// 修改API路径
const API_BASE = '/admin/api/users';

// 用户搜索功能
function searchUsers() {
    const keyword = $('#searchInput').val().trim();
    if (keyword) {
        window.location.href = '/admin/users?keyword=' + encodeURIComponent(keyword);
    } else {
        window.location.href = '/admin/users';
    }
}

// 编辑用户
function editUser(userId) {
    // 获取用户信息并显示编辑模态框
    $.get('/admin/users/' + userId, function(response) {
        if (response.code === 200) {
            const user = response.data;
            $('#editUserId').val(user.id);
            $('#editPhone').val(user.phone);
            $('#editEmail').val(user.email);
            $('#editMemberLevel').val(user.memberLevel);
            $('#editUserModal').modal('show');
        } else {
            alert('获取用户信息失败：' + response.message);
        }
    });
}

// 保存用户修改
function saveUser() {
    // 构建用户对象，移除会员等级
    const user = {
        id: $('#editUserId').val(),
        phone: $('#editPhone').val(),
        email: $('#editEmail').val()
    };

    // 发送请求
    $.ajax({
        url: '/admin/users/update',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(user),
        success: function(response) {
            if (response.code === 200) {
                alert('保存成功');
                $('#editUserModal').modal('hide');
                location.reload();
            } else {
                alert('保存失败：' + response.message);
            }
        },
        error: function(xhr, status, error) {
            alert('保存失败：' + error);
            console.error('Error details:', xhr.responseText);
        }
    });
}

// 删除用户
function deleteUser(userId) {
    if (confirm('确定要删除该用户吗？')) {
        $.ajax({
            url: '/admin/users/' + userId,
            type: 'DELETE',
            success: function(response) {
                if (response.code === 200) {
                    alert('删除成功');
                    location.reload();
                } else {
                    alert('删除失败：' + response.message);
                }
            }
        });
    }
}

// 切换用户状态
function toggleUserStatus(userId, currentStatus) {
    const newStatus = currentStatus === 1 ? 0 : 1;
    const action = currentStatus === 1 ? '禁用' : '启用';
    
    if (confirm(`确定要${action}该用户吗？`)) {
        $.ajax({
            url: '/admin/users/' + userId + '/status',
            type: 'PUT',
            data: JSON.stringify({ status: newStatus }),
            contentType: 'application/json',
            success: function(response) {
                if (response.code === 200) {
                    alert(`${action}成功`);
                    location.reload();
                } else {
                    alert(`${action}失败：` + response.message);
                }
            }
        });
    }
}

// 回车搜索
$('#searchInput').on('keypress', function(e) {
    if (e.which === 13) {
        searchUsers();
    }
});

// 清空搜索
function clearSearch() {
    $('#searchInput').val('');
    window.location.href = '/admin/users';
} 