function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (!username || !password) {
        alert('请输入用户名和密码');
        return;
    }

    fetch('/admin/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            // 登录成功，重定向到仪表板
            window.location.href = data.extra.redirectUrl || '/admin/dashboard';
        } else {
            // 显示错误消息
            alert(data.message || '登录失败');
        }
    })
    .catch(error => {
        console.error('Login error:', error);
        alert('登录过程中发生错误');
    });
} 