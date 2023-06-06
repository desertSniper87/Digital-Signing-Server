function callAsyncAjax(url, formData, type = "POST") {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: type,
            url: url,
            data: formData,
            dataType: 'JSON',
            success: function (data) {
                resolve(data)
            },
            error: function (jqXHR, textStatus, errorThrown) {
                reject(errorThrown)
            }
        });
    })
}

function callAsyncAjax2(url, formData, type = "POST") {
    return $.ajax({
        type: type,
        url: url,
        data: formData,
        dataType: 'JSON',
    });
}