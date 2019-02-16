function getVektLoggForm(){
	var form = document.createElement("form");
	form.setAttribute("autocomplete","off");
	form.onsubmit = function(){
		request("type=insertVekt"+getVektFormData(),"Kosthold/Vekt",function(){
			handleInsertResponse(this.response);
		})
		return false;
	};

	var loggSubmit = document.createElement("input");
	loggSubmit.setAttribute("type","submit")

	var kiloInput = document.createElement("input");
	kiloInput.setAttribute("id","vektInput")
	kiloInput.setAttribute("type","number")
	kiloInput.setAttribute("step","0.01")

	var kiloForklaring = getDiv("inlineDiv");
	kiloForklaring.innerText = "kg";

	form.appendChild(kiloInput);
	form.appendChild(kiloForklaring);
	form.appendChild(loggSubmit);
	return form;
}

function getVektFormData(){
	let output = "&kilo=";
	let input = document.getElementById("vektInput");
	return output+input.value;
}
/* TODO statsTabellFraJson burde være dynamisk */
function getVektStats(){
	let containerDiv = getDiv(null,"statsContainer");
	request("type=getVektLogg","Kosthold/Vekt",function(){
		let data = JSON.parse(this.response);
		let table =  getTableFromJSON(data);
		document.getElementById("statsContainer").appendChild(table);
	});
	return containerDiv;
}

