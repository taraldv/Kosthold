function getBenevningsMålForm(){
	let form = document.createElement("form");
	form.onsubmit = function(){
		request("type=endreBenevningMål"+getBenevningsMålFormData(),"Kosthold/Innstillinger",function(){
			handleInsertResponse(this.response);
		})
		return false;
	}

	let benevningContainer = getDiv(false,"benevningContainer");

	let headerRow = getDiv(false,"næringstoffForklaringDiv");
	headerRow.innerHTML = "<div>næringstoff</div><div>øvre</div><div>nedre</div><div>aktiv</div>"

	benevningContainer.appendChild(headerRow);

	let submit = document.createElement("input");
	submit.setAttribute("type","submit");

	form.appendChild(submit);
	form.appendChild(benevningContainer);

	return form;
}

function getInputRow(innhold,id,øvre,nedre,aktiv,benevning){
	let benevningDiv = getDiv("inlineDiv benevningDiv");
	benevningDiv.innerText = benevning;

	let innholdInput = document.createElement("input");
	innholdInput.setAttribute("type","text");
	innholdInput.setAttribute("disabled","");
	innholdInput.setAttribute("data-id",id);
	innholdInput.setAttribute("class","næringstoffInnhold");
	innholdInput.value = innhold;

	let øvreInput = document.createElement("input");
	øvreInput.setAttribute("type","number");
	øvreInput.value = øvre;

	let nedreInput = document.createElement("input");
	nedreInput.setAttribute("type","number");
	nedreInput.value = nedre;
	
	let checkBox = document.createElement("input");
	checkBox.setAttribute("type","checkbox");
	checkBox.checked = aktiv;
	checkBox.setAttribute("class","næringstoffSjekk")

	let rowDiv = getDiv("næringstoffRad");
	rowDiv.appendChild(innholdInput);
	rowDiv.appendChild(øvreInput);
	rowDiv.appendChild(nedreInput);
	rowDiv.appendChild(benevningDiv)
	rowDiv.appendChild(checkBox);

	return rowDiv;
}

function getBenevningsMålFormData(){
	let output = "";
	let containerDiv = document.getElementById("benevningContainer");
	let children = containerDiv.children;
	/* første div inneholder forklaringer */
	for(let i=1;i<children.length;i++){
		let inputList = children[i].children;

		let innhold = "&id="+inputList[0].getAttribute("data-id");
		let øvre =  "&øvreMål="+inputList[1].value;
		let nedre = "&nedreMål="+inputList[2].value;
		let aktiv = "&aktiv="+inputList[4].checked;

		output += innhold+øvre+nedre+aktiv
	}
	return output;
}

function insertBenevningFromPost(){

	/* TODO: sorter object med tanke på aktiv og navn? */
	let data = JSON.parse(this.response);
	let containerDiv = document.getElementById("benevningContainer");
	var length = Object.keys(data).length;
	for(let i=0;i<length;i++){
		
		let innhold = data[i].næringsinnhold;
		let øvre = parseInt(data[i].øvreMål);
		let nedre = parseInt(data[i].nedreMål);
		let aktiv = (parseInt(data[i].aktiv)==1);
		let benevning = data[i].benevning;
		let id = data[i].benevningId

		/* en slags sortering, aktiv blir prepend */
		let rowDiv = getInputRow(innhold,id,øvre,nedre,aktiv,benevning);

		let firstProperNode = containerDiv.firstChild.nextSibling;
		if(aktiv && firstProperNode){
			containerDiv.insertBefore(rowDiv,firstProperNode);
		} else {
			containerDiv.appendChild(rowDiv);
		}
		
	}
}