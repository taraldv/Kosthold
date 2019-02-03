/* generer form som brukes til å logge kosthold fra matvaretabellen */
function getLoggForm(){
	var form = document.createElement("form");
	form.setAttribute("autocomplete","off");
	form.onsubmit = function(){
		request("type=insertLogg"+getCollectionDataForForm("matvareCollection"),"Kosthold/Logg",function(){
			handleInsertResponse(this.response);
		})
		return false;
	};

	var loggSubmit = document.createElement("input");
	loggSubmit.setAttribute("type","submit")

	var matvareCollection = getDiv("collection","matvareCollection");

	form.appendChild(getMatvareInputWithAutocompleteDiv("matvaretabellen","matvareCollection"));
	form.appendChild(loggSubmit);
	form.appendChild(matvareCollection);
	return form;
}

/* generer en div med knapper som kan trykkes for å legge en lagret måltid til loggen */
function getMåltidButtons(){

	var mainDiv = getDiv(false,"måltidButtonsDiv");
	request("type=getMåltider","Kosthold/Måltid",function(){
		var json = JSON.parse(this.response);
		var length = Object.keys(json).length;
		var måltidButtonsDiv = document.getElementById("måltidButtonsDiv");
		for(i=0;i<length;i++){
			var tempDiv = getDiv("måltidButton");
			tempDiv.setAttribute("data-id",json[i].måltidId);
			tempDiv.innerText = json[i].navn;
			tempDiv.addEventListener("click",function(){
				var måltidId = this.getAttribute("data-id");
				request("type=getMåltiderIngredienser&måltidId="+måltidId,"Kosthold/Måltid",matvareCollectionInsert);
			});
			måltidButtonsDiv.appendChild(tempDiv);
		}
	});

	return mainDiv;
}