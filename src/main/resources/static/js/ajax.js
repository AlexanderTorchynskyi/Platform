/**
 * Created by Keno&Kemo on 10.12.2017..
 */
const getJsonUsers = "/admin/json-users";
const token = $('#_csrf').attr('content');
const header = $('#_csrf_header').attr('content');

let productIdToDelete;
let rowIndexToDelete;

$.ajaxSetup({
    headers: {
        // 'Content-Type':  'application/json',
        // 'Accept': 'application/json',
        // 'X-CSRF-TOKEN': token
    }
});

function setRowIndexAndProductId(row, id) {
    productIdToDelete = id;
    rowIndexToDelete = row.parentNode.parentNode.rowIndex;
}

function closeModal(nameOfTheModal) {
    $(nameOfTheModal).modal('toggle');
}

function deleteEntity() {
    let deleteUserUrl = '/api/products/' + productIdToDelete;

    $.ajax({
        url: deleteUserUrl,
        type: 'DELETE',
        success: function () {

            let table = $("#main-table");
            table[0].deleteRow(rowIndexToDelete);

            $('#delete-alert').append(
                "<div class='alert alert-success alert-dismissible fade show' role='alert'>"+
                "<button type='button' class='close' data-dismiss='alert' aria-label='Close'>"+
                "<span aria-hidden='true'>&times;</span> </button>"+
                "Product has been deleted"+
                "</div>"
            );
            closeModal('#deleteModal');
            productIdToDelete = "";
            rowIndexToDelete = "";
        }
    });
}


