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

		for(let j=0;j<dataLength;j++){
			let keys = Object.keys(data[j]);
			let tempTR = getElement("tr","table-row");
			
			tempTR.setAttribute("data-id",data[j][keys[0]]);
			for(let i=1;i<keys.length;i++){
				let tempCell = getElement("td","table-cell");
				let cellText = data[j][keys[i]];


				/*logikk for å hoppe over text i celle hvis cellen over har samme text*/
				if(j>0){
					for(let y=j;y>0;y--){
						let previousRow = table.childNodes[y];
						let previousRowCellText = previousRow.childNodes[i-1].innerText;
						if(previousRowCellText==cellText){
							/*hvis forrige celle er lik, la denne cellen være tom*/
							tempCell.classList.add("blank-table-cell");
							break;
						} else if(previousRowCellText==""){
							/*hvis forrige celle er blank, fortsett loop bakover*/
							continue;
						} else {
							tempCell.innerText = cellText;
							break;
						}
					}
				} else {
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