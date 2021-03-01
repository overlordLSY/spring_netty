let demoWebSocket
let hostname = window.location.hostname

// 右上角弹窗提示
const Toast = Swal.mixin({
  toast: true,
  position: 'top-end',
  showConfirmButton: false,
  timer: 3000,
  didOpen: (toast) => {
    toast.addEventListener('mouseenter', Swal.stopTimer)
    toast.addEventListener('mouseleave', Swal.resumeTimer)
  }
})
function toastInfo(msg) {
  Toast.fire({
    icon: 'info',
    title: msg
  })
}
function toastSuccess(msg) {
  Toast.fire({
    icon: 'success',
    title: msg
  })
}
function toastError(msg) {
  Toast.fire({
    icon: 'error',
    title: msg
  })
}
function toastWarning(msg) {
  Toast.fire({
    icon: 'warning',
    title: msg
  })
}
function toastQuestion(msg) {
  Toast.fire({
    icon: 'question',
    title: msg
  })
}

// 连接WebSocket
function webSocketConnect(port, path, userId) {
  if (!window.WebSocket) {
    window.WebSocket = window.MozWebSocket;
  }
  if (window.WebSocket) {
    let url = "ws://" + hostname + ":" + port + path
    if (userId !== undefined && userId !== '') {
      url = url + "?userId=" + userId
    }

    demoWebSocket = new WebSocket(url);
    demoWebSocket.onmessage = function (event) {
      let json = JSON.parse(event.data)
      if (json.message !== undefined && json.message !== '') {
        toastInfo(json.message)
      }
    };
  } else {
    toastError('您的浏览器不支持WebSocket，无法接收推送')
  }
}

// WebSocket发送信息
function webSocketSend(userId, message) {
  if (!window.WebSocket) {
    return;
  }
  if (demoWebSocket.readyState === WebSocket.OPEN) {
    let msg = JSON.stringify({
      "userId": userId,
      "message": message
    })
    demoWebSocket.send(msg);
  } else {
    toastError('WebSocket连接失败')
  }
}
