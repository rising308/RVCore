function load_navbar() {
    fetch('/utility/navbar.html')
        .then(response => response.text())
        .then(html => {
            document.getElementById('navbar-container').innerHTML = html;
        });
}
load_navbar();
