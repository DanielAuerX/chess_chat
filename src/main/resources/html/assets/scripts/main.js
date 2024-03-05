const origin = window.location;

window.onload = function () {
  configure("idle");

  let senders = document.getElementsByClassName("sender");
  for (let i = 0; i < senders.length; i++) {
    let sender = senders[i];
    sender.addEventListener("keyup", function (event) {
      event.preventDefault();
      if (event.keyCode === 13) {
        console.log(sender.value);
        message(sender.value);
        sender.value = "";
      }
    });

  document.getElementById("discord").addEventListener("click", function () {
    openURL("https://discord.gg/G56HbBfk");
  });

  fetch(origin + "v1/config/websocket")
    .then((response) => response.text())
    .then((data) => {
      connect("ws://127.0.0.1:" + data);
    })
    .catch((error) => {
      console.error("Error:", error);
    });

  const visibility = document.getElementById("visibility");

  var options = document.getElementsByClassName("option");
  for (let i = 0; i < options.length; i++) {
    options[i].addEventListener("click", (e) => {
      if (!e.target.classList.contains("disabled")) {
        let active = getActiveSAAS();
        if (!active.includes("settings")) {
          settings.dataset.previous = active;
        }
        document.getElementById("return").classList.remove("disabled");
        for (let i = 0; i < options.length; i++) {
          options[i].classList.add("disabled");
        }
        if (e.target.id == "settings") {
          if (document.getElementById("jamachi").dataset.status === "host") {
            if (visibility.classList.contains("hidden"))
              visibility.classList.remove("hidden");
          } else {
            if (!visibility.classList.contains("hidden"))
              visibility.classList.add("hidden");
          }
        }
        hideAllSAAS("page-" + e.target.id);
      }
    });
  }

  document.getElementById("return").addEventListener("click", (e) => {
    if (!e.target.classList.contains("disabled")) {
      hideAllSAAS(settings.dataset.previous);
      document.getElementById("return").classList.add("disabled");
      for (let i = 0; i < options.length; i++) {
        options[i].classList.remove("disabled");
      }
    }
  });

  const mainpage = document.getElementById("mainpage");
  mainpage.addEventListener("click", function () {
    reset();
    openLandingPage();
  });

  var range = document.querySelector("input[type=range]");
  var listener = function () {
    window.requestAnimationFrame(function () {
      adjustAudioGain(range.value);
    });
  };
  range.addEventListener("mousedown", function () {
    listener();
    range.addEventListener("mousemove", listener);
  });
  range.addEventListener("mouseup", function () {
    range.removeEventListener("mousemove", listener);
  });
  range.addEventListener("keydown", listener);

  document.getElementById("select-join").addEventListener("click", function () {
    configure("attendee");
    discover();
    hideAllSAAS("page-join");
  });
  document.getElementById("join").addEventListener("click", function () {
    join(document.getElementById("partyid").value);
  });

  document.getElementById("skip").addEventListener("click", function () {
    skip();
  });

  var showlist = document.getElementsByClassName("showlist");
  for (let i = 0; i < showlist.length; i++) {
    showlist[i].addEventListener("click", function () {
      console.log("clicked");
      let display = document.getElementById("jamachi").dataset.display;
      if (display == "userlist")
        document.getElementById("jamachi").dataset.display = "chat";
      else document.getElementById("jamachi").dataset.display = "userlist";
      if (display == null) {
        document.getElementById("jamachi").dataset.display = "userlist";
      }
      display = document.getElementById("jamachi").dataset.display;
      let first = display == "userlist" ? "messagebox" : "userlist";
      let second = display == "userlist" ? "userlist" : "messagebox";
      var arr1 = document.getElementsByClassName(first);
      for (let i = 0; i < arr1.length; i++) {
        arr1[i].classList.add("hidden");
      }
      var arr2 = document.getElementsByClassName(second);
      for (let i = 0; i < arr2.length; i++) {
        arr2[i].classList.remove("hidden");
      }
    });
  }

  document.getElementById("set-name").addEventListener("click", function () {
    username(
      document.getElementById("username").value,
      document.getElementById("partyid").value
    );
  });

  document.getElementById("copy").addEventListener("click", function () {
    copyToClipboard(document.getElementById("party").innerHTML);
  });

  document.getElementById("invite").addEventListener("click", function () {
    copyToClipboard(
      "https://jamalo.ng/" + document.getElementById("party").innerHTML
    );
  });

  document.getElementById("select-host").addEventListener("click", function () {
    configure("host");
    host();
    hideAllSAAS("page-host");
  });

  const input = document.getElementById("sclink");
  input.addEventListener("keyup", function (event) {
    event.preventDefault();
    if (event.keyCode === 13) {
      load(input.value);
      input.value = "";
    }
  });
};

function openLandingPage() {
  gatekeep(true);
  configure("idle");
  var options = document.getElementsByClassName("option");
  for (let i = 0; i < options.length; i++) {
    document.getElementById("return").classList.add("disabled");
    for (let i = 0; i < options.length; i++) {
      options[i].classList.remove("disabled");
    }
  }

function configure(current) {
  document.getElementById("jamachi").dataset.status = current;
}

function getActiveSAAS() {
  var elements = document.getElementsByClassName("saas");
  for (let i = 0; i < elements.length; i++) {
    var element = elements[i];
    if (!element.classList.contains("hidden")) {
      return element.id;
    }
  }
  return "page-landing";
}

function call(url) {
  fetch(url).catch((error) => {
    console.error("Error:", error);
  });
}
function username(name, partyId) {
  let party = partyId.length === 0 ? "nil" : partyId;
  call(origin + "v1/api/namechange/" + btoa(name) + "/" + party);
}

function message(msg) {
  call(origin + "v1/api/chat/" + btoa(encodeURIComponent(msg)));
}

function connect(host) {
  let socket = new WebSocket(host);
  socket.onopen = function (msg) {
    console.log("Connected to " + host);
    hideAllSAAS("page-landing");
  };
  socket.onmessage = function (msg) {
    console.log(msg.data);
    const json = JSON.parse(msg.data);
    console.log(json);
    if (json.hasOwnProperty("instruction")) {
      switch (json["instruction"]) {
        case "chat":
          let boxes = document.getElementsByClassName("messagebox");
          for (let i = 0; i < boxes.length; i++) {
            let box = boxes[i];
            let div = document.createElement("div");
            let span = document.createElement("span");
            span.classList = "msg-user";
            span.innerHTML = json["user"];
            span.title = json["identifier"];
            div.appendChild(span);
            let msg = document.createElement("span");
            if (!json.hasOwnProperty("type")) {
              msg.innerHTML = ": " + json["message"];
            } else {
              msg.innerHTML = " " + json["message"];
            }
            const currentDate = new Date();
            const formattedTimestamp = currentDate.toLocaleString();
            msg.title = formattedTimestamp;
            div.appendChild(msg);
            box.appendChild(div);
            box.scrollTo(0, box.scrollHeight);
          }
          break;
        case "rediscover":
          discover();
          break;
        case "reset-gatekeeper":
          gatekeep(true);
          break;
        case "gatekeeper":
          gatekeep(json["status"]);
          break;
        case "reveal":
          document.getElementById("nowplaying").innerHTML = json["name"];
          break;
        case "close":
          openLandingPage();
          break;
        case "download":
          updateDownload(json["progress"]);
          break;
        case "kill":
          openLandingPage();
          break;
        case "list":
          var users = json["users"];
          var hosts = document.getElementsByClassName("hoster");
          for (let i = 0; i < hosts.length; i++) {
            hosts[i].innerHTML = users[0];
          }

          var userlists = document.getElementsByClassName("userlist");
          for (let i = 0; i < userlists.length; i++) {
            let userlist = userlists[i];
            userlist.innerHTML = "";
            for (let i = 1; i < users.length; i++) {
              let div = document.createElement("div");
              div.innerHTML = users[i];
              userlist.appendChild(div);
            }
          }

          var totals = document.getElementsByClassName("total");
          for (let i = 0; i < totals.length; i++) {
            totals[i].innerHTML = users.length;
          }
          break;
      }
    } else if (json.hasOwnProperty("result")) {
      document.getElementById("partyid").value = json["result"].split(" ")[0];
      hideAllSAAS("page-chat");
    }
  };
  socket.onclose = function (msg) {
    console.log("disconnected from " + host);
  };
}

function build(owner, partyId, users) {
  const roomDiv = document.createElement("div");
  roomDiv.classList.add("room");

  const roomDetailDiv = document.createElement("div");
  roomDetailDiv.classList.add("room-detail", "flex", "bar", "mini-margin");

  const flexContainer1 = document.createElement("div");
  flexContainer1.classList.add("flex", "gap");

  const userIconDiv = document.createElement("div");
  userIconDiv.innerHTML = '<i class="fa-solid fa-user fa-xl"></i>';
  flexContainer1.appendChild(userIconDiv);

  const hostDiv = document.createElement("div");
  hostDiv.classList.add("host");
  hostDiv.textContent = users;
  flexContainer1.appendChild(hostDiv);

  roomDetailDiv.appendChild(flexContainer1);

  const flexContainer2 = document.createElement("div");
  flexContainer2.classList.add("flex", "gap");

  const roomIdDiv = document.createElement("div");
  roomIdDiv.classList.add("room-id");
  roomIdDiv.textContent = owner;
  flexContainer2.appendChild(roomIdDiv);

  const clipboardIconDiv = document.createElement("div");
  clipboardIconDiv.innerHTML =
    '<i class="selectable fa-solid fa-door-open"></i>';
  clipboardIconDiv.addEventListener("click", function () {
    document.getElementById("partyid").value = partyId;
    join(partyId);
  });

  flexContainer2.appendChild(clipboardIconDiv);

  roomDetailDiv.appendChild(flexContainer2);

  roomDiv.appendChild(roomDetailDiv);

  return roomDiv;
}

function updateDownload(progress) {
  document.getElementById("update").value = progress;
  document.getElementById("visual").innerHTML = progress + "%";
}

function copyToClipboard(text) {
  const textArea = document.createElement("textarea");
  textArea.value = text;
  document.body.appendChild(textArea);
  textArea.select();
  document.execCommand("copy");
  document.body.removeChild(textArea);
}
