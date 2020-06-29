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
var slideIndex = 0;

function initSlides() {
	showSlides(slideIndex);
}

// Next control
function showNextSlide() {
	slideIndex++;
	showSlides(slideIndex);
}

// Previous control
function showPreviousSlide() {
	slideIndex--;
	showSlides(slideIndex);
}

// Thumbnail image controls
function updateCurrentSlide(n) {
	slideIndex = n;
	showSlides(slideIndex);
}

function showSlides(n) {
	var slides = document.getElementsByClassName("mySlides");
	var dots = document.getElementsByClassName("dot");
	slideIndex = n % slides.length;
	if (slideIndex == -1) {
		slideIndex = slides.length - 1;
	}
	for (const slide of slides) {
		slide.style.display = "none";
	}
	for (const dot of dots) {
		dot.classList.remove("active");
	}
	slides[slideIndex].style.display = "block";
	dots[slideIndex].classList.add("active");
}

initSlides();

/**
 * Adds a random car and its data to the page.
 */
function getRandomCar() {
	const cars = [
		{
			name: "McLaren 720S",
			power: "720 hp",
			country: "England",
			funFact: "Just look at it, it is beautiful!",
			path: "images/720s.jpg"
		},
		{
			name: "Porsche 918 Spyder",
			power: "720 hp",
			country: "Germany",
			funFact: "Hybrid power for the win!",
			path: "images/porsche.jpeg"
		},
		{
			name: "Ferrari FXX Evoluzione",
			power: "848 hp",
			country: "Italy",
			funFact: "You should really listen to the sound this one makes",
			path: "images/ferrari.jpg"
		},
		{
			name: "Lamborghini Sesto Elemento",
			power: "562 hp",
			country: "Italy",
			funFact: "The whole thing weights less that a tonne thanks to carbon fiber!",
			path: "images/lambo.jpg"
		},
		{
			name: "Mercedes-AMG G63 6x6",
			power: "536 hp",
			country: "Germany",
			funFact: "Taking offroading to a whole new level.",
			path: "images/mercedes.jpeg"
		},
		{
			name: "Subaru WRX STI",
			power: "536 hp",
			country: "Japan",
			funFact: "Boxster engines rock!",
			path: "images/subaru.jpg"
		},
		{
			name: "Mini JCW",
			power: "306 hp",
			country: "England",
			funFact: "I currently own a Mini :).",
			path: "images/mini.jpeg"
		},
		{
			name: "Volkswagen Sedan",
			power: "Unlimited hp",
			country: "Germany",
			funFact: "Grandpa's car <3, learned to drive in it.",
			path: "images/vw.jpg"
		}
	];

	// Pick a random car.
	const car = cars[Math.floor(Math.random() * cars.length)];

	// Add it to the page.
	const carImageContainer = document.getElementById('js-car-image');
	carImageContainer.src = car.path;

	const carNameContainer = document.getElementById('js-car-name-container');
	carNameContainer.innerText = "Name: " + car.name;

	const carPowerContainer = document.getElementById('js-car-power-container');
	carPowerContainer.innerText = "Power: " + car.power;

	const carCountryContainer = document.getElementById('js-car-country-container');
	carCountryContainer.innerText = "Country: " + car.country;

	const carFunFactContainer = document.getElementById('js-car-funFact-container');
	carFunFactContainer.innerText = "Why I like this car? " + car.funFact;
}
