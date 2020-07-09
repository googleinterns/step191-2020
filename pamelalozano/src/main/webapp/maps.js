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

/** Creates a map that shows landmarks around Google. */
async function createMapChart(){

  google.charts.load('current', {
        'packages':['geochart'],
        // Note: you will need to get a mapsApiKey for your project.
        // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings
        'mapsApiKey': 'AIzaSyAcFI9_xPk0RWlT8w42YQfNyUhQbApYw3I'
  });
    
      google.charts.setOnLoadCallback(drawRegionsMap);

      async function drawRegionsMap() {
        var data = google.visualization.arrayToDataTable(await fetchInfo());

        var options = {};

        var chart = new google.visualization.GeoChart(document.getElementById('map'));

        chart.draw(data, options);
      }
}

async function fetchInfo(){

    var output = [];
    output.push(['Country', 'Female employees in industry %']);

    let response = await fetch('https://api.worldbank.org/V2/country/all/indicator/SL.IND.EMPL.FE.ZS?format=json&date=2019&page=1&per_page=264');
    let json = await response.json();
    let info = json[1];

    let j = 1;
    info.forEach((data)=>{
        if(data.value != null){    
             output.push([]);
            if(data.country.value != "Russian Federation"){
                output[j].push(data.country.value);
            } else {
                output[j].push("Russia");
            }
                output[j].push(parseInt(data.value.toFixed(2)));
                j++;
            }
    })

    output.splice(1, 46);
    return output;
}