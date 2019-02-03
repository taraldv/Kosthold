
/* genererer form til å lagre ett nytt måltid med ingredienser fra matvaretabellen */
function getMåltidForm(){
	var form = document.createElement("form");
	form.setAttribute("autocomplete","off");
	form.onsubmit= function(){
		var måltidNavnInput = document.getElementById("måltidNavnInput");
		var navn = måltidNavnInput.value;
		if(navn.length>1){
			request("type=insertMåltider"+"&navn="+navn+getCollectionDataForForm("måltidCollection"),"Kosthold/Måltid",function(){
				handleInsertResponse(this.response);
			})					
		}
		return false;
	};
	form.setAttribute("id","måltidForm");

	var måltidNavnInput = document.createElement("input");
	måltidNavnInput.setAttribute("type","text");
	måltidNavnInput.setAttribute("id","måltidNavnInput");

	var måltidNavnBeskrivelse = document.createElement("div");
	måltidNavnBeskrivelse.innerText = "måltid navn";

	var måltidNavnBeskrivelseDiv = getDiv("inlineDiv")
	måltidNavnBeskrivelseDiv.appendChild(måltidNavnBeskrivelse);
	måltidNavnBeskrivelseDiv.appendChild(måltidNavnInput);

	var måltidSubmit = document.createElement("input");
	måltidSubmit.setAttribute("type","submit");

	var måltidCollectionDiv = getDiv("collection","måltidCollection")

	let yadOne = getDiv();
	yadOne.appendChild(måltidNavnBeskrivelseDiv);
	yadOne.appendChild(måltidSubmit);

	let yadTwo = getDiv();
	yadTwo.appendChild(getMatvareInputWithAutocompleteDiv("matvaretabellen","måltidCollection"));
	yadTwo.appendChild(måltidCollectionDiv);

	form.appendChild(yadOne);
	form.appendChild(yadTwo);

	return form;
}
