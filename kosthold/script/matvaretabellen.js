

/* henter all data fra 'innholdDiv' som blir brukt til å legge til en ny matvare i matvaretabellen */
function getMatvaretabellFormData(){
	var output = "";
	var matvaretabellInputDiv = document.getElementById("innholdDiv");
	var children = matvaretabellInputDiv.children;
	for(i=0;i<children.length;i++){
		var tempChildren = children[i].children;
		var text = tempChildren[0].firstChild.value;
		var number = tempChildren[1].value;
		if(!number){
			number = 0;
		}
		if(!text){
			text = 0;
		}
		output += "&matvaretabellMatvare"+i+"="+text+"&maatvaretabellInnhold"+i+"="+number
	}
	return output;
}

/* generer en form som blir brukt til å legge til en ny matvare i matvaretabellen */
function getMatvaretabellForm(){

	var standard = [["Kilojoule","kJ"],
	["Kilokalorier","kcal"],
	["Fett","g"],
	["Mettet","g"],
	["Enumettet","g"],
	["Flerumettet","g"],
	["Karbohydrat","g"],
	["Sukker, tilsatt","g"],
	["Kostfiber","g"],
	["Protein","g"],
	["Salt","g"]];

	var form = document.createElement("form");
	form.setAttribute("autocomplete","off");
	form.onsubmit = function(){
		var matvaretabellInputNavn = document.getElementById("matvaretabellInputNavn");
		var navn = matvaretabellInputNavn.value;
		request("type=insertMatvaretabell"+"&navn="+navn+getMatvaretabellFormData(),"Kosthold/Matvare",function(){
			handleInsertResponse(this.response);
		})
		return false;
	};

	var matvaretabellSubmit = document.createElement("input");
	matvaretabellSubmit.setAttribute("type","submit");

	var addButton = getDiv("divButton");
	addButton.innerText = "Legg til innhold";
	addButton.addEventListener("click",function(){
		leggTilInnholdForMatvare();
	})

	var innholdDiv = getDiv(false,"innholdDiv");


	var matvaretabellInputNavnBeskrivelse = getDiv();
	matvaretabellInputNavnBeskrivelse.innerText = "matvare navn";

	var matvaretabellInputNavn = document.createElement("input");
	matvaretabellInputNavn.setAttribute("type","text");
	matvaretabellInputNavn.setAttribute("id","matvaretabellInputNavn");

	var matvaretabellInputNavnBeskrivelseDiv = getDiv("inlineDiv");
	matvaretabellInputNavnBeskrivelseDiv.appendChild(matvaretabellInputNavnBeskrivelse);
	matvaretabellInputNavnBeskrivelseDiv.appendChild(matvaretabellInputNavn);

	form.appendChild(matvaretabellInputNavnBeskrivelseDiv);
	form.appendChild(addButton);
	form.appendChild(matvaretabellSubmit);
	form.appendChild(innholdDiv);

	for(i = 0;i<standard.length;i++){
		innholdDiv.appendChild(leggTilInnholdForMatvare(standard[i]));
	}

	return form;
}


/* legger til en tom div med 2 input for å velge en type innhold til ny matvare i matvaretabellen (Salt etc) */
function leggTilInnholdForMatvare(arr){
	var typeInput = document.createElement("input");
	typeInput.setAttribute("type","text");
	typeInput.setAttribute("data-tabell","næringsinnhold");
	typeInput.addEventListener("keyup",autocomplete);
	typeInput.addEventListener("focusin",autocompleteFocusIn)
	typeInput.addEventListener("focusout",autocompleteFocusOut);

	var verdiInput = document.createElement("input");
	verdiInput.addEventListener("focusin",removeAutocompleteDiv);
	verdiInput.setAttribute("type","number");
	verdiInput.setAttribute("step",".01");
	verdiInput.setAttribute("class","numberInput");

	var autocompleteContainer = getDiv("autocompleteContainerDiv");
	autocompleteContainer.appendChild(typeInput);

	var containerDiv = getDiv("innholdDivs");
	containerDiv.appendChild(autocompleteContainer);
	containerDiv.appendChild(verdiInput);

	/* bruker arr til å legge til de mest vanlige */
	if(arr){
		typeInput.value = arr[0];
		typeInput.setAttribute("disabled","");
		verdiInput.placeholder = arr[1];
		return containerDiv;
	}

	document.getElementById("innholdDiv").appendChild(containerDiv);
}


function getMatvaretabellTabell(){
	let containerDiv = getDiv(null,"statsContainer");
	request("type=getVektLogg","Kosthold/Vekt",function(){
		let data = JSON.parse(this.response);
		let table =  getTabelFromJSON(data);
		document.getElementById("statsContainer").appendChild(table);
	});
	return containerDiv;
}