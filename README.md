# Recursive BFS WebCralwer Solution for CPSC 215: Software Development Foundations Assignment 1

## Description

This repository contains the solution to Assigment 1: Webcralwer for the course CPSC 215: Software Development Foundations that I taught in Spring 2014 at Clemson University.

The WebCrawler is an application that, when given a URL, requests the contents of the file encoded by the URL.  It then parses the return file, noting any other webpages, images and non-image/non-webpage files linked from the initial webpage.  Upon completing its parsing, the search-and-parse routine recurses, searching any webpages found, until a depth specified by the user is reached.  Once the search has recursed deep enough, the program downloads any files, images and webpages it encountered.

## Installation

1. Clone the code to your computer.
2. Compile the source using javac / java or your favorite IDE (I used Eclipse).

## Usage
1. Execute the source via your favorite method (jar, compiled java, etc) with the following syntax:
<execution method> <URL> <Depth> <SaveLocation>

## Contributing

Please do not contribute to this project.  This compiler was a pedagogical proof of concept to illustrate the language theoretic constructs delivered in CPSC 827: Translation of Programming Languages.  This is an instructor solution to an old project I assigned when I was teaching undergraduate software development. 

## Credits

Credit goes to me, myself, Yates Monteith, for this project.  Additional credit goes Jason O. Hallstrom and Sally Wahba for iterating on the requirements of this assignment.

## License

GNU GPL v2.0.  See License
