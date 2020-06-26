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

/**
 * Adds a random car and its data to the page.
 */
function getRandomCar() {
	const cars = [
		{
			name: "McLaren 720S",
			power: "720 hp",
			country: "England",
			funFact: "Just look at it, it is beautiful!"
		},
		{
			name: "Porsche 918 Spyder",
			power: "720 hp",
			country: "Germany",
			funFact: "Hybrid power for the win!"
		},
		{
			name: "Ferrari FXX Evoluzione",
			power: "848 hp",
			country: "Italy",
			funFact: "You should really listen to the sound this one makes"
		},
		{
			name: "Lamborghini Sesto Elemento",
			power: "562 hp",
			country: "Italy",
			funFact: "The whole thing weights less that a tonne thanks to carbon fiber!"
		},
		{
			name: "Mercedes-AMG G63 6x6",
			power: "536 hp",
			country: "Germany",
			funFact: "Taking offroading to a whole new level."
		},
		{
			name: "Subaru WRX STI",
			power: "536 hp",
			country: "Japan",
			funFact: "Boxster engines rock!"
		},
		{
			name: "Mini JCW",
			power: "306 hp",
			country: "England",
			funFact: "I currently own a Mini :)."
		},
		{
			name: "Volkswagen Sedan",
			power: "Unlimited hp",
			country: "Germany",
			funFact: "Grandpa's car <3, learned to drive in it."
		}
    ];

    // Pick a random car.
    const car = cars[Math.floor(Math.random() * cars.length)];

    // Add it to the page.
    const carNameContainer = document.getElementById('car-name-container');
    carNameContainer.innerText = "Name: " + car.name;

    const carPowerContainer = document.getElementById('car-power-container');
    carPowerContainer.innerText = "Power: " + car.power;

    const carCountryContainer = document.getElementById('car-country-container');
    carCountryContainer.innerText = "Country: " + car.country;

    const carFunFactContainer = document.getElementById('car-funFact-container');
    carFunFactContainer.innerText = "Why I like this car? " + car.funFact;
}

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Adds Armandos age to the page.
 */
function calculateArmandoAge() {
    // Calculate age, Armando was born on May 13, 2000.
    const diffMs = new Date() - new Date(2000, 5, 13);
    const ageDt = new Date(diffMs); 
    
    // Add it to the page.
    const ageContainer = document.getElementById('armando-age-container');
    ageContainer.innerText = Math.abs(ageDt.getUTCFullYear() - 1970);
    
}

// Scripts for the image gallery
var slideIndex = 1;

function initSlides() {
  showSlides(slideIndex);
}

// Next/previous controls
function plusSlides(n) {
  showSlides(slideIndex += n);
}

// Thumbnail image controls
function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("dot");
  if (n > slides.length) {slideIndex = 1}
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
      slides[i].style.display = "none";
  }
  for (i = 0; i < dots.length; i++) {
      dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex-1].style.display = "block";
  dots[slideIndex-1].className += " active";
}
