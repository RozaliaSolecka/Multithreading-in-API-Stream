# Multithreading-in-API-Stream
> The application was prepared for the subject Technological Platforms. 

## Table of contents
* [About The Project](#about-the-project)
* [Technologies](#technologies)
* [Getting Started](#getting-started)
* [Contact](#contact)

## About The Project
The application allows the user to select a set of image files from the disk and converts them tograyscale images. Image files are processed as
parallel with API Stream.

File processing variants:
* sequential processing,
* concurrent processing using the default thread pool (commonPool),
* concurrent processing where the number of worker threads is specified
by the user.

Options:
* selecting many files from the disk and displaying them in the table,
* sequentially processing files with an update of the operation progress for each
the image in the table,
* parallel file processing,
* comparing processing times for sequential stream and streams
concurrent with different numbers of threads.

## Technologies
* Java API Stream

## Getting Started
Clone the repository  
`git clone https://github.com/RozaliaSolecka/Multithreading-in-API-Stream.git` 
  
Open project in your favourite IDE.   
  
IntelliJ IDEA:  
See: https://www.jetbrains.com/idea/

## Contact
Rozalia Solecka - rozaliasolecka@gmail.com
