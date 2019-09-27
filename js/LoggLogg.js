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
function insertRequest(buttonId,type,url,parameterArray,tableStuff,deleteStuff,interval){
	document.getElementById(buttonId).addEventListener('click',(e)=>{
		let btn = e.target;
		let children = btn.parentNode.childNodes;

		//parameterArray og inputArray skal være like store
		let inputArray = [];
		//leter etter select og inputs
		for(let i=0;i<children.length;i++){
			let tempTag = children[i].tagName;
			if(tempTag=='SELECT'){
				let selected = children[i].options[children[i].selectedIndex];
				inputArray.push(selected.getAttribute('data-id'));
			} else if(tempTag=='LABEL'){
				let input = children[i].lastChild;
				inputArray.push(input.value);
			}
		}
		let dataString = "";
		for(let j=0;j<parameterArray.length;j++){
			dataString+="&"+parameterArray[j]+"="+inputArray[j];
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
//stuff er array med [typeNavn,idNavn,url]
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