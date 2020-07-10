// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Creates a map according to the info of the indicator in the world's bank data */

document.getElementById("searchIndicatorBtn").addEventListener("click",(e) => {
    document.getElementById("map").innerHTML="";
    let  form = document.getElementById("selectIndicator");
    let selected = form.options[form.selectedIndex];
    let year = checkIndicatorYear(selected.value);
    document.getElementById("indicatorTitle").innerText=selected.text+" "+year;
    createMapChart(selected.value, selected.text, year);
})

/* Returns the year with the last available information of each indicator*/
function checkIndicatorYear(indicator) {
  switch (indicator) {
      case 'ER.PTD.TOTL.ZS':
      return 2018;
      case 'EN.ATM.CO2E.PC':
      return 2014;
      default:
      return 2019;
  }
}

async function createMapChart(indicatorCode, indicatorName, year){

  google.charts.load('current', {
        'packages':['geochart'],
        // Note: you will need to get a mapsApiKey for your project.
        // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings
        'mapsApiKey': 'AIzaSyAcFI9_xPk0RWlT8w42YQfNyUhQbApYw3I'
  });
    
      google.charts.setOnLoadCallback(drawRegionsMap);
    console.log(indicatorCode+" "+indicatorName)
      async function drawRegionsMap() {

        //ToDo: The indicator code will be the name of the selected indicator in the input element  

        var data = google.visualization.arrayToDataTable(await fetchInfo(indicatorCode, indicatorName, year));

        var options = {};

        var chart = new google.visualization.GeoChart(document.getElementById('map'));

        chart.draw(data, options);
      }
}

/*Returns the information in a 2d array */
async function fetchInfo(indicatorCode, indicatorName, year){

    var output = [];

    const url = new URL(`https://api.worldbank.org/V2/country/all/indicator/${indicatorCode}`);
    const params = url.searchParams;
    params.set('format', 'json');
    params.set('date', year);
    params.set('page', 1);
    params.set('per_page', 264);
    
    let response = await fetch(url);
    let json = await response.json();
    let info = json[1];

    //From 1 to 46 the results are zone names not country names so they are not useful
    info.splice(0, 46);
    console.log(info);

    output.push(['Country', indicatorName]);

    for (let data of info) {
        if (data.value != null) {
            const country = normalizeCountryName(data.country.value);
            const value = parseInt(data.value.toFixed(2));
            output.push([country, value]);
        }
    }

    console.log(output);
    return output;
}

/* Returns the name of the country that the chart api accepts */
function normalizeCountryName(name){
    if(name != "Russian Federation") {
          return name;
    } else {
        return "Russia";
    };    
}
