function removeChildren(myNode){
	while (myNode.firstChild) {
		myNode.removeChild(myNode.firstChild);
	}
}
function request(data,url,func){
	var oReq = new XMLHttpRequest();
	oReq.addEventListener('load',func);
	oReq.open("POST",url);
	oReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
	oReq.withCredentials = true;
	oReq.send(data);	
}

function attachAutocompleteToClass(inputClass){
	let elements = document.getElementsByClassName(inputClass);
	for(let i=0;i<elements.length;i++){
		elements[i].addEventListener('keyup',autocomplete);
	}
}

function attachAutocompleteToId(divId){
	let input = document.getElementById(divId);
	input.addEventListener('keyup',autocomplete);
}

/*function getAutocompleteDiv(json){
	var autocompleteDiv = getElement('div',"autocompleteDivClass","autocompleteDivId");

	var searchWord = json.search;
	var length = Object.keys(json).length;

	for(i=0;i<length-1;i++){
		var keys = Object.keys(json[i]);
		var tempDiv = document.createElement("div");
		tempDiv.setAttribute("data-id",json[i][keys[1]]);
		var wholeString = json[i][keys[0]];
		tempDiv.innerHTML = highlightString(wholeString,searchWord)

		tempDiv.addEventListener("click",function(){
			var parent = this.parentNode.parentNode;
			console.log(parent);
			var firstChild = parent.firstChild;
			firstChild.value = this.innerText;
			var id = this.getAttribute("data-id");
			if(isNaN(parseInt(id))){
				var mengdeInput = parent.nextSibling;
				mengdeInput.placeholder = id;
			} else {
				firstChild.setAttribute("data-id",id);
			}			
			removeAutocompleteDiv();
		})
		autocompleteDiv.appendChild(tempDiv);
	}
	return autocompleteDiv;
}*/

function appendAutocompleteData(input,div,json){
	var searchWord = json.search;
	var length = Object.keys(json).length;

	for(i=0;i<length-1;i++){
		var keys = Object.keys(json[i]);
		var tempDiv = document.createElement("div");
		tempDiv.setAttribute("data-id",json[i][keys[1]]);
		var wholeString = json[i][keys[0]];
		tempDiv.innerHTML = highlightString(wholeString,searchWord);

		tempDiv.addEventListener("click",function(){
			input.value = this.innerText;
			div.remove();
			input.focus();
		});
		div.appendChild(tempDiv);
	}
}

/* Funksjon som blir kjørt når keypress aktiveres i valgt inputs */
function autocomplete(event){
	let keyPressed = event.key;
	let activeInput = this;

	if(keyPressed=="Enter"){
		
	}

	let labelParent = this.parentNode;
	let autocompleteDiv = labelParent.lastChild;
	let autocompleteDivClass = autocompleteDiv.getAttribute('class');
	if(autocompleteDivClass == "autocompleteDiv"){
		autocompleteDiv.remove();
	} 
	autocompleteDiv = getElement('div','autocompleteDiv','');
	labelParent.appendChild(autocompleteDiv);
	
	//console.log(activeInput.value);
	request("type=autocomplete&string="+activeInput.value,"/kosthold/matvaretabellen/",function(){
		var obj = JSON.parse(this.response);
		//var whichInputActivatedAutocomplete = document.getElementById("activatedAutocomplete");
		appendAutocompleteData(activeInput,autocompleteDiv,obj);
	});
	
}

/* noe med måltider */
function test(divId,url){
	request("type=getMåltider",url,function(){
		let data = JSON.parse(this.response);
		let keys = Object.keys(data);
		for(let i=0;i<keys.length;i++){
			let tempDivId = "måltidContainerId"+i;
			let forklaring = getElement("h3","","");
			forklaring.innerText = data[i].navn;
			let tempContainerDiv = getElement("div","måltiderContainer","");
			let tempDiv = getElement("div","ingrediensContainer",tempDivId);
			tempContainerDiv.appendChild(forklaring);
			tempContainerDiv.appendChild(tempDiv);
			document.getElementById(divId).appendChild(tempContainerDiv);
			let tempId = data[i].måltidId;
			let miniTableStuff = ["getMåltiderIngredienser&måltidId="+tempId,tempDivId,'/kosthold/måltider/'];
			let miniTableDeleteStuff = ['deleteMåltidIngrediens','ingredienseId','/kosthold/måltider/'];
			buildTable(miniTableStuff,miniTableDeleteStuff,0);
			let deleteMåltidDiv = getElement("div","","");
			deleteMåltidDiv.innerText = "Slett";
			deleteMåltidDiv.addEventListener("click",(e)=>{
				request("type=deleteMåltider&måltidId="+tempId,url,function(){
					/* TODO error og dynamisk */
					console.log(this.response);
				});
			});
			tempContainerDiv.appendChild(deleteMåltidDiv);
		}
	});
}

/* Knytter valgt element til ett xmlhttprequest med valgt parameters */
function insertRequest(buttonId,type,url,parameterArray,tableStuff,deleteStuff,interval){
	document.getElementById(buttonId).addEventListener('click',(e)=>{
		let btn = e.target;
		let children = btn.parentNode.childNodes;

		//leter etter select og inputs
		let x = 0;
		let dataString = "";
		for(let i=0;i<children.length;i++){
			let tempTag = children[i].tagName;
			if(tempTag=='SELECT'){
				let selected = children[i].options[children[i].selectedIndex];
				let innhold = selected.getAttribute('data-id');
				dataString+="&"+parameterArray[x]+"="+innhold;
				x++;
			} else if(tempTag=='LABEL'){
				let input = children[i].lastChild;
				let verdi = input.value;	
				dataString+="&"+parameterArray[x]+"="+verdi;
				x++;
			}
			if (parameterArray.length==x) {
				break;
			}
		}

		/* leter gjennom barns barn etter select/input
		som ikke har forklaringer i paramterarray,
		server bruker Map*/
		for(let i=0;i<children.length;i++){
			let childrenChildren = children[i].childNodes;
			for(let j=0;j<childrenChildren.length;j++){
				let tempTag = childrenChildren[j].tagName;
				if(tempTag=='SELECT'){
					let selected = childrenChildren[j].options[childrenChildren[j].selectedIndex];
					let innhold = selected.getAttribute('data-id');
					dataString+="&innhold="+innhold;
				} else if(tempTag=='LABEL'){
					let input = childrenChildren[j].lastChild;
					let verdi = input.value;	
					dataString+="&verdier="+verdi;
				}
			}
		}
		
		request("type="+type+dataString,url,function(){
			if(this.response==1){
				buildTable(tableStuff,deleteStuff,interval);
			} else {
				/*TODO handle error*/
				console.log(this.response);
			}	
		});
	});
}

function buildTableHeader(obj){
	let headerRow = getElement("tr","header-row");
	let keys = Object.keys(obj);
	//begynner på 1 for å hoppe over id
	for(let x=1;x<keys.length;x++){
		let tempTh = getElement("th","header-cell");
		tempTh.innerText = keys[x];
		headerRow.appendChild(tempTh);
	}
	return headerRow;
}


function attachServerRequestToSelect(type,data,selectId,url,containerId,removeId){
	let select = document.getElementById(selectId);
	select.addEventListener('change',(e)=>{
		if(removeId){
			let elemToBeRemoved = document.getElementById(removeId);
			elemToBeRemoved.parentNode.removeChild(elemToBeRemoved);
		}
		let selected = select.options[select.selectedIndex];
		request("type="+type+"&"+data+"="+selected.innerText,url,function(){
			document.getElementById(containerId).insertAdjacentHTML('beforeend', this.response);
		});
	});
}

//knytter et server request til en button event
function attachServerRequestToButton(type,buttonId,url,containerId){
	let btn = document.getElementById(buttonId);
	btn.addEventListener('click',(e)=>{
		request("type="+type,url,function(){
			document.getElementById(containerId).insertAdjacentHTML('beforeend', this.response);
		});
	});
}

//tableStuff er array med [type,elementAppendId,url]
//deleteStuff er array med [type,sqlKolonneId,url]
function buildTable(tableStuff,deleteStuff,interval){
	let table = getElement("table","data-table");
	request("interval="+interval+"&type="+tableStuff[0],tableStuff[2],function(){
		let data = JSON.parse(this.response);
		let dataLength = Object.keys(data).length;
		
		table.appendChild(buildTableHeader(data[0]));
		//array som inneholder siste j, hvor cellen hadde nytt innhold
		let lastContentIndexArray = new Array(Object.keys(data[0]).length).fill(0);

		for(let j=0;j<dataLength;j++){
			let keys = Object.keys(data[j]);
			let tempTR = getElement("tr","table-row");
			
			tempTR.setAttribute("data-id",data[j][keys[0]]);
			for(let i=1;i<keys.length;i++){
				let tempCell = getElement("td","table-cell");
				let cellText = data[j][keys[i]];
				let previousCellText = data[lastContentIndexArray[i-1]][keys[i]];
				/*logikk for å hoppe over text i celle hvis cellen over har samme text*/
				if(j>0 && cellText==previousCellText){
					tempCell.classList.add("blank-table-cell");
				} else {
					lastContentIndexArray[i-1] = j;
					tempCell.innerText = cellText;
				}
				tempTR.appendChild(tempCell);
			}
			let deleteButton = getElement("td","table-delete-cell");
			deleteButton.innerText = "Slett";
			deleteButton.addEventListener('click',(e)=>{
				let dataId = e.target.parentNode.getAttribute('data-id');
				request("type="+deleteStuff[0]+"&"+deleteStuff[1]+"="+dataId,deleteStuff[2],function(){
					if(this.response==1){
						buildTable(tableStuff,deleteStuff,interval);
					} else {
						/*TODO handle error*/
						console.log(this.response);
					}	
				});
			});
			tempTR.appendChild(deleteButton);
			table.appendChild(tempTR);

		}
	});
	let div = document.getElementById(tableStuff[1]);
	removeChildren(div);
	div.appendChild(table);
}

function getElement(elemName,elemClass,elemId){
	var elem = document.createElement(elemName);
	elem.setAttribute("class",elemClass);
	if(elemId){
		elem.setAttribute("id",elemId);
	}
	return elem;
}

function toggleMenuHide(clickId,hideId,toggleClass){
	let clickElem = document.getElementById(clickId);
	clickElem.addEventListener('click',(e)=>{
		let hideElement = document.getElementById(hideId);
		hideElement.classList.toggle(toggleClass);
	});
}


/* brukes av autocomplete */
function highlightString(original, stringToMatch){
	var index = original.toLowerCase().indexOf(stringToMatch.toLowerCase());
	if(index>=0){
		var beforeString = original.slice(0,index);
		var slicedString = original.slice(index,index+stringToMatch.length);
		var afterString = original.slice(index+stringToMatch.length);
		return beforeString.concat("<strong>"+slicedString+"</strong>",afterString);
	} else {
		return original;
	}
}
