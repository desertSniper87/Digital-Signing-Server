
var xmlString = "<test>" +
	"  <one>" +
	"    <two att=\"xxx\">abcd1234</two>" +
	"    <three boo=\"yah\" att=\"yyy\">1234abcd</three>" +
	"    <two>another</two>" +
	"  </one>" +
	"  <number>1234</number>" +
	"</test>";

$("#inputXml").val(xmlString);


var currentFileId = '';

$("document").ready(function() {

	/*$(".btn-digital-xml").on("click", async function() {
		isSupported();

		let signedResponse = await DigitalSigner.signXml($("#inputXml").val(), reason = "The reason of signing");
		console.log(signedResponse)
		$("#result").text(signedResponse.OutputData);
		$("#message").text(signedResponse.Msg);

	})*/
	$(".btn-esign-select").on("click", async function() {
		let formData = { "userId": $("#select-quickpass-username").val() };
		console.log(formData);
		let url = "/web/select-cert";
		try {
			let response = await callAsyncAjax2(url, formData);
			console.log(response)
			$("#message").text(response.message);
			$("#result").text(response.data);
		} catch (err) {
			console.log(err);
			$("#message").text(err.responseJSON.message);

		}
	})

	$(".btn-esign-pdf").on('click', function() {
		let documentSelector = $("#pdfDocument");
		var fd = new FormData();
		var files = documentSelector[0].files; // Check file selected or not 
		if (files.length > 0) {
			let username = $("#quickpass-username").val();
			fd.append('userId', username);
			let leftX =  $("#leftx").val();
			fd.append('leftX', leftX);
			let leftY = $("#lefty").val();
			fd.append('leftY', leftY);
			let rightX = $("#rightx").val();
			fd.append('rightX', rightX);
			let rightY = $("#righty").val();
			fd.append('rightY', rightY);

			let pageNumber = $("#pageNo").val();
			fd.append('pageNumber', pageNumber);
			let reason = $("#reason").val();
			fd.append('reason', reason);
			let location = $("#location").val();
			fd.append('location', location);

			fd.append('document', files[0]);

			console.log(fd);

			$.ajax({
				url: 'web/sign',
				type: 'post',
				data: fd,
				contentType: false,
				processData: false,
				success: function(response) {
					console.log(response)
					let link = '<br><a href="' + response.data.url + '">Download Signed document</a>';
					$("#message").html(response.message + link);
					$("#result").text(JSON.stringify(response.data));
				},
				error: function(err) {
					console.log(err);
					$("#message").text(err.responseJSON.message);
					$("#result").text(JSON.stringify(err.responseJSON));
				}
			})
		} else {
			alert("Please select a file.");
		}
		return false;
	});


	var init = () => {
		$("#message").text('Welcome to eSign Signing Demo');
		$("#result").html("");
	}

	init();
})