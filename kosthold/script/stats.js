function getStatsDiv(){
	var mainDiv = getDiv(false,"statsDiv");

	request("type=getLogg","Kosthold/Logg",function(){
		var dataObject = parseLoggData(JSON.parse(this.response));
		mainDiv.appendChild(getStatsDivTable(dataObject.daily,"daglig"));
		mainDiv.appendChild(getStatsDivTable(dataObject.weekly,"ukentlig / 7"));
		mainDiv.appendChild(getStatsDivTable(dataObject.monthly,"månedlig / 31"));
		mainDiv.appendChild(getDagligLoggDiv(dataObject.logg));
		request("type=getLoggMål","Kosthold/Logg",colorStatsDivTable);
	})


	return mainDiv;
}


function colorStatsDivTable(){
	let data = JSON.parse(this.response);
	var tableElements = document.getElementsByClassName("statsTabell");
	for(let obj in data){
		for(let x=0;x<tableElements.length;x++){
			let tableRows = tableElements[x].children;
			for(let i=1;i<tableRows.length;i++){
				let tableDataElements = tableRows[i].children;
				for(let j=0;j<tableDataElements.length;j++){
					let dataNavn = tableDataElements[j].getAttribute("dataNavn");
					let dataMengde = parseFloat(tableDataElements[j].getAttribute("dataMengde"));
					if(data[obj].næringsinnhold == dataNavn){
						if(dataMengde > data[obj].nedreMål && dataMengde < data[obj].øvreMål){
							tableDataElements[j].setAttribute("class","greenTD");
						} else {
							tableDataElements[j].setAttribute("class","redTD")
						}
					}
				}
			}
		}
	}
}

function getStatsDivTable(sumObject,tableCaption){
	let mainDiv = getDiv();
	let table = document.createElement("table");
	table.setAttribute("class","statsTabell");
	let cap = document.createElement("caption");
	cap.innerText=tableCaption;
	table.appendChild(cap);
	let keys = Object.keys(sumObject);
	let y = 4;
	let antallRader = parseInt(keys.length/y);
	if(keys.length % y > 0){
		antallRader += 1;
	}
	for(let i=0;i<antallRader;i++){
		var tempRad = document.createElement("tr");
		for(let j=0;j<y;j++){
			let index = i*y+j;
			if(keys[index]){
				let tempTD = document.createElement("td");
				tempTD.setAttribute("dataNavn",keys[index]);
				let tempDiv = getDiv("");
				let tempBeskrivelseDiv = getDiv("");
				tempBeskrivelseDiv.innerText = keys[index];
				let tempMengdeDiv = getDiv("");
				/* runder av til 1 desimal */
				let tempValue = Math.round(sumObject[keys[index]] * 10) / 10;
				tempMengdeDiv.innerText = tempValue;
				tempTD.setAttribute("dataNavn",keys[index]);
				tempTD.setAttribute("dataMengde",tempValue);
				tempDiv.appendChild(tempBeskrivelseDiv);
				tempDiv.appendChild(tempMengdeDiv);
				tempTD.appendChild(tempDiv);
				tempRad.appendChild(tempTD)
			}
		}
		table.appendChild(tempRad);
	}

	mainDiv.appendChild(table);
	return mainDiv;
}

function parseLoggData(data){

	var length = Object.keys(data).length;
	var output = {logg:[],daily:{},weekly:{},monthly:{}};

	main:
	for(i=0;i<length;i++){
		var næringsinnholdObjekt = sumNæringsinnholdFraObject(data[i]);

		var dataMatvare = data[i].matvare;
		var dataMengde = data[i].mengde;
		var dataDato = data[i].dato;

		/*en dag = 86400000 millisekunder*/
		var innen24Timer = sqlDateWithinMilliseconds(dataDato,86400000);
		var innen1Uke = sqlDateWithinMilliseconds(dataDato,86400000*7);
		var innen1Mnd =sqlDateWithinMilliseconds(dataDato,86400000*31);


		if(innen24Timer){
			mergeObjects(output.daily,næringsinnholdObjekt);
			/* går igjennom array for å finne duplikat */
			for(j=0;j<output.logg.length;j++){
				var loggMatvare = output.logg[j][0];
				if(dataMatvare == loggMatvare){
					/* øker mengden hvis duplikat i stedet for å legge til ny */
					output.logg[j][1] += parseFloat(dataMengde);
					continue main;
				}
			}
			/* legger til matvare hvis den ikke finnes i array */
			output.logg.push([dataMatvare,parseFloat(dataMengde)]);
		}
		if(innen1Uke){
			mergeObjects(output.weekly,næringsinnholdObjekt);
		}
		if(innen1Mnd){
			mergeObjects(output.monthly,næringsinnholdObjekt);
		}
	}
	averageObjects(output.weekly,7);
	averageObjects(output.monthly,31);
	return output;
}

function averageObjects(obj,heltall){
	for (let key in obj){
		obj[key] = obj[key]/heltall;
	}
}

function mergeObjects(mainObj,subObj){
	let subKeys = Object.keys(subObj);
	let subLength = subKeys.length;
	for(let k=0;k<subLength;k++){
		let tempKey = subKeys[k];
		if(mainObj[tempKey]){
			mainObj[tempKey] += subObj[tempKey];
		} else {
			mainObj[tempKey] = subObj[tempKey];
		}
	}
}


function getDagligLoggDiv(loggArr){
	var dateToday = formatDateToSQL(Date.now());
	var mainDiv = getDiv();
	var table = document.createElement("table");
	var caption = document.createElement("caption");
	caption.innerText = dateToday;
	table.appendChild(caption);
	var headerRow = document.createElement("tr");
	var matvareHeader = document.createElement("th");
	matvareHeader.innerText = "matvarer";
	var mengdeHeader = document.createElement("th");
	mengdeHeader.innerText = "mengde";
	headerRow.appendChild(matvareHeader);
	headerRow.appendChild(mengdeHeader);
	table.appendChild(headerRow);
	for(i=0;i<loggArr.length;i++){
		var row = document.createElement("tr");
		var matvareData = document.createElement("td");
		matvareData.innerText = loggArr[i][0];
		var mengdeData = document.createElement("td");
		mengdeData.innerText = loggArr[i][1];
		row.appendChild(matvareData);
		row.appendChild(mengdeData);
		table.appendChild(row);
	}
	mainDiv.appendChild(table);
	return mainDiv;
}

function getNæringsTabell(arr,captionTekst){
	var table = document.createElement("table");
	var caption = document.createElement("caption");
	caption.innerText = captionTekst;
}

function sqlDateWithinMilliseconds(string,millis){
	var dateInMilli = Date.now();
	var sqlDatoMilli = Date.parse(string);
	return (dateInMilli-sqlDatoMilli<millis)
}

function sumNæringsinnholdFraObject(obj){
	let næringsinnholdSummert = {};
	let keys = Object.keys(obj);
	let length = keys.length;
	let mengde = obj.mengde;
	for(x=0;x<length;x++){
		if(keys[x] != "matvare" && keys[x] != "mengde" && keys[x] != "dato"){
			næringsinnholdSummert[keys[x]] = (parseFloat(mengde)/100)*obj[keys[x]];
		}
	}
	return næringsinnholdSummert;
}



function formatDateToSQL(date){
	var dato = new Date(date);
	var dag = dato.getDate();
	var år = dato.getFullYear();
	var mnd = dato.getMonth()+1;
	if(mnd<10){
		mnd = "0"+mnd;
	}
	if(dag<10){
		dag = "0"+dag;
	}
	return år+"-"+mnd+"-"+dag;
}
