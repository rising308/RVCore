function load_navbar() {
    fetch('/utility/navbar.html')
        .then(response => response.text())
        .then(html => {
            document.getElementById('navbar-container').innerHTML = html;

            set_active();
        });
}
load_navbar();
function set_active() {
    const path = location.href.split("/").slice(-1).toString();

    const index = document.querySelector(".indexButton");
    const download = document.querySelector(".downloadButton");
    const about = document.querySelector(".aboutButton");

    switch (path) {
        case "index.html":
            index.className += " active";
            break;
        case "downloads.html":
            download.className += " active";
            break;
        case "about.html":
            about.className += " active";
            break;
        default:
            break;
    }
}
