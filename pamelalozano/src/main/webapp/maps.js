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
async function createMapChart(){

  google.charts.load('current', {
        'packages':['geochart'],
        // Note: you will need to get a mapsApiKey for your project.
        // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings
        'mapsApiKey': 'AIzaSyAcFI9_xPk0RWlT8w42YQfNyUhQbApYw3I'
  });
    
      google.charts.setOnLoadCallback(drawRegionsMap);

      async function drawRegionsMap() {

        //ToDo: The indicator code will be the name of the selected indicator in the input element  
        let indicatorCode =  'SL.IND.EMPL.FE.ZS';

        var data = google.visualization.arrayToDataTable(await fetchInfo(indicatorCode));

        var options = {};

        var chart = new google.visualization.GeoChart(document.getElementById('map'));

        chart.draw(data, options);
      }
}

/*Returns the information in a 2d array */
async function fetchInfo(indicatorCode){

    var output = [];
    output.push(['Country', 'Female employees in industry %']);

    const url = new URL(`https://api.worldbank.org/V2/country/all/indicator/${indicatorCode}`);
    const params = url.searchParams;
    params.set('format', 'json');
    params.set('date', 2019);
    params.set('page', 1);
    params.set('per_page', 264);
    
    let response = await fetch(url);
    let json = await response.json();
    let info = json[1];

    for (let data of info) {
        if (data.value != null) {
            const country = normalizeCountryName(data.country.value);
            const value = parseInt(data.value.toFixed(2));
            output.push([country, value]);
        }
    }

    //From 1 to 46 the results are zone names not country names so they are not useful
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
