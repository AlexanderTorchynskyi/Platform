$(document).ready(function () {
    changePageAndSize();
});

let qs = decodeURIComponent(location);
let menuItem = qs.replace("http://localhost:8080", "");

//making active links
$("a[href='" + menuItem + "']").parent("li").addClass("active");

if (menuItem.includes("admin")) {
    $("a[href='/admin']").parent("li").addClass("active");
}
if (menuItem.includes("products")) {
    $("a[href='/admin/products']").parent("li").addClass("active");
}

function changePageAndSize() {

    $('#pageSizeSelect').change(function (evt) {
    window.location.replace("/admin/products/?pageSize=" + this.value + "&page=1");
    });

}