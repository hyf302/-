// 一键清除功能
function clearReservations() {
    console.log('clearReservations function called');
    
    if (!confirm('确定要清除所有已取消的预约记录吗？')) {
        console.log('User cancelled the operation');
        return;
    }
    
    const userId = getCurrentUserId();
    console.log('Current User ID:', userId);
    
    if (!userId) {
        console.error('Failed to get user ID');
        alert('获取用户信息失败，请重新登录');
        return;
    }

    // 构建请求参数
    const params = new URLSearchParams();
    params.append('userId', userId);
    params.append('status', '3');

    const url = `/api/reservations/clear?${params.toString()}`;
    console.log('Request URL:', url);

    console.log('Sending DELETE request...');
    axios.delete(url)
        .then(response => {
            console.log('Response received:', response);
            if (response.data && response.data.code === 200) {
                console.log('Operation successful');
                alert('清除成功');
                window.location.reload();
            } else {
                console.error('Operation failed:', response.data);
                alert(response.data?.message || '清除失败');
            }
        })
        .catch(error => {
            console.error('Request failed:', error);
            if (error.response) {
                console.error('Error Response:', error.response);
                alert('清除失败：' + (error.response.data?.message || '请求失败'));
            } else if (error.request) {
                console.error('Error Request:', error.request);
                alert('清除失败：服务器未响应');
            } else {
                console.error('Error:', error.message);
                alert('清除失败：' + error.message);
            }
        });
}

// 获取当前用户ID的函数
function getCurrentUserId() {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    return userInfo ? userInfo.id : null;
}

function calculatePrice(visitorCount) {
    const originalPrice = scenicSpot.price * visitorCount;
    
    // 调用后端计算折扣价格
    axios.post('/api/reservations/calculate-price', {
        userId: getCurrentUserId(),
        scenicSpotId: scenicSpot.id,
        visitorCount: visitorCount,
        totalPrice: originalPrice
    })
    .then(response => {
        if (response.data.code === 200) {
            const priceInfo = response.data.data;
            document.getElementById('originalPrice').textContent = '原价：￥' + priceInfo.originalPrice;
            document.getElementById('discountedPrice').textContent = '会员价：￥' + priceInfo.discountedPrice;
        }
    })
    .catch(error => {
        console.error('计算价格失败:', error);
    });
}

// 获取预约记录
function loadReservations(status) {
    $.ajax({
        url: '/api/reservations/my',
        type: 'GET',
        success: function(response) {
            if (response.code === 200) {
                let reservations = response.data;
                let html = '';
                
                // 根据状态过滤预约记录
                if (status) {
                    reservations = reservations.filter(r => r.status === status);
                }
                
                // 如果没有预约记录
                if (reservations.length === 0) {
                    $('#reservation-list').html('<div class="text-center">暂无预约记录</div>');
                    return;
                }

                // 遍历预约记录生成HTML
                reservations.forEach(function(reservation) {
                    html += generateReservationHtml(reservation);
                });
                
                $('#reservation-list').html(html);
            } else {
                alert(response.message || '获取预约记录失败，请刷新重试');
            }
        },
        error: function() {
            alert('获取预约记录失败，请刷新重试');
        }
    });
}

// 生成预约记录HTML
function generateReservationHtml(reservation) {
    let statusText = getStatusText(reservation.status);
    let statusClass = getStatusClass(reservation.status);
    
    return `
        <div class="reservation-item">
            <div class="scenic-name">${reservation.scenicSpotName}</div>
            <div class="reservation-info">
                <p>预约日期：${reservation.reservationDate}</p>
                <p>时间段：${reservation.timeSlot}</p>
                <p>游客数：${reservation.visitorCount}</p>
                <p>总价：￥${reservation.totalPrice}</p>
                <p>实付：￥${reservation.actualPrice}</p>
                <p>状态：<span class="${statusClass}">${statusText}</span></p>
            </div>
            ${generateOperationButtons(reservation)}
        </div>
    `;
}

// 获取状态文本
function getStatusText(status) {
    const statusMap = {
        1: '待支付',
        2: '已支付',
        3: '已取消',
        4: '已完成'
    };
    return statusMap[status] || '未知状态';
}

// 获取状态样式类
function getStatusClass(status) {
    const classMap = {
        1: 'status-pending',
        2: 'status-paid',
        3: 'status-cancelled',
        4: 'status-completed'
    };
    return classMap[status] || '';
}

// 生成操作按钮
function generateOperationButtons(reservation) {
    let buttons = '';
    
    if (reservation.status === 1) { // 待支付
        buttons += `
            <button onclick="payReservation(${reservation.id})" class="btn-pay">立即支付</button>
            <button onclick="cancelReservation(${reservation.id})" class="btn-cancel">取消预约</button>
        `;
    } else if (reservation.status === 2) { // 已支付
        buttons += `
            <button onclick="refundReservation(${reservation.id})" class="btn-refund">申请退款</button>
        `;
    } else if (reservation.status === 4 && !reservation.hasReview) { // 已完成且未评价
        buttons += `
            <button onclick="showReviewDialog(${reservation.id})" class="btn-review">评价</button>
        `;
    }
    
    return buttons ? `<div class="operation-buttons">${buttons}</div>` : '';
}

// 页面加载完成后执行
$(document).ready(function() {
    // 加载所有预约记录
    loadReservations();
    
    // 绑定状态筛选事件
    $('.filter-btn').click(function() {
        $('.filter-btn').removeClass('active');
        $(this).addClass('active');
        
        const status = $(this).data('status');
        loadReservations(status);
    });
}); 
 