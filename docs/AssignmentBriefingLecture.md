{\rtf1\ansi\ansicpg1252\cocoartf2759
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 # Software Design\
## Assessment 2024/25\
\
---\
\
1.  Assessment Briefing\
\
---\
Lecture 18: Assessment 2024/25\
18.1\
Software Design\
---\
\
## ****** WARNING ******\
\
Following is an **overview** of some of the main aspects of the **assignment**.\
\
**BUT:**\
*   The snippets of the assignment are **NOT *the whole assignment specification***\
    *   They are bits that are useful to talk about in this briefing\
    *   Aspects that we sometimes get questions about\
\
**So:**\
*   You **MUST** refer to the formal, complete assignment specification when undertaking the assignment\
    *   On the module page\
\
**If:**\
*   You think there is any contradiction between what is said here and the assignment specification, the specification is always correct.\
    *   But, in that case, please ask for clarification.\
\
---\
Lecture 18: Assessment 2024/25\
18.2\
Software Design\
---\
\
## Assignment\
\
|                     |                                                                                                |\
| :------------------ | :--------------------------------------------------------------------------------------------- |\
| **Module Code**     | **Module Name**                                                                                |\
| ELE000551           | Software Design                                                                                |\
| **Assessment Name** | **% contribution**                                                                             |\
| Project Code        | 65                                                                                             |\
| Individual Report   | 35                                                                                             |\
\
**Accepted submission formats**\
\
Two files in the following accepted file formats:\
Project Code: ZIP file containing code and associated files as indicated in the main assessment requirement.\
Individual Report: Single Portable Document Format (PDF)\
\
Submission is via VLE.\
\
---\
Lecture 18: Assessment 2024/25\
18.3\
Software Design\
---\
\
## Assignment\
\
**1 Task: Flocking Simulation**\
\
You are to design and implement a flocking simulation in Java using object-oriented programming techniques and Java libraries that have been introduced in lectures and labs. You **must** directly use the Canvas and geometry classes developed in your \'93drawing\'94 and \'93geometry\'94 packages from the laboratories. You **must** use the Canvas class as-is from the module wiki \'96 no modifications or subclasses are allowed. You may add to the \'93geometry\'94 classes as appropriate, but any additions should **not** supersede the original functionality, particularly the method signatures and instance fields. Failure to use these Canvas and geometry classes as required will result in an overall mark of **zero** for the \'93Project Code\'94 component of the assignment. You should also make use of the classes/code developed in the "turtle\'94 package \'96 you should adapt these as appropriate for the task.\
\
Your application **must** simulate flocking behaviour in a two-dimensional world displayed graphically on the computer screen. Every individual in the flock should be able to move around and interact with others by following a set of simple rules that result in **emergent** flocking behaviour. The implemented rules **must** control the individuals via the \'93move(pixels)\'94 and \'93turn(angle)" methods in a similar way to the basic control implemented in the \'93update\'94 method during the laboratories. The original \'93move\'94 and \'93turn\'94 method signatures **must** be as in the original laboratory template, keeping the parameters as integer values.\
\
Your program **must** allow for the number of individuals and the flocking parameters to be varied by the user through interacting directly with your application's graphical interface.\
\
---\
Lecture 18: Assessment 2024/25\
18.4\
Software Design\
---\
\
## Assignment\
\
Your program **must** allow for the number of individuals and the flocking parameters to be varied by the user through interacting directly with your application's graphical interface.\
\
There are three rectangular obstacles on the canvas: size (dx=120, dy=80) with top-left corner at (100, 300), size (dx=80, dy=150) with top-left corner at (350, 200), size (dx=150, dy=120) with top-left corner at (550, 100). These are impervious to any individuals \'96 they cannot move into them or through them. You **must** implement these obstacles as part of the base specification.\
\
**To achieve high marks, the project should demonstrate good object-oriented design practices**, such as appropriate inheritance and polymorphism, along with modular well-written code with good documentation and a well written and well-structured report.\
\
**To achieve high marks the program should be extended to include other complexities**, such as control over simulation speed, obstacles, collision detection or other types of individuals for the flock to interact with (perhaps add predators into the simulation from which prey flee and/or diseased individuals which may or may not recover). However, a simple program that works is better than an unfinished, overly ambitious attempt that does not compile or execute correctly.\
\
---\
Lecture 18: Assessment 2024/25\
18.5\
Software Design\
---\
\
## Assignment\
\
You are to write your program in standard Java, version 11 and it must compile and execute correctly on the laboratory computers used in the module, using the Java command line tools or Eclipse.\
\
Submission will be via the VLE in two parts:\
\
1.  **Individual Report:** a PDF report of **maximum** eight A4 pages in length. Anything beyond the limit will **not** be considered. A title page is not required but, if included, will not count towards the limit. The report should cover all software design aspects including flocking algorithm, complex data structures / algorithms, testing, etc. \'96 see the mark scheme below for more detail.\
2.  **Project Code:** A zip file containing:\
    *   The Java source code for your program (appropriately commented and properly formatted).\
    *   A simple text file named README.txt that lists which source files should be compiled (simply stating "all" is acceptable if appropriate) and the entry point (main class) used to run your final program.\
\
---\
Lecture 18: Assessment 2024/25\
18.6\
Software Design\
---\
\
## Assignment\
\
The marks are broken down into the categories below (all marks in percentages). The **Project Code** will be marked under \'93Object-Oriented Design\'94 and \'93Program Implementation\'94, the **Individual Report** will be marked under \'93Report\'94.\
\
**Appropriate Object-Oriented Design Practices (30)**\
*   Use of multiple classes (6)\
*   Use of composition within your classes (4)\
*   Use of inheritance within your class structure (4)\
*   Use of polymorphism when referring to your objects (4)\
*   Use of interfaces within your class structure (4)\
*   Use of inner and/or anonymous classes (4)\
*   Use of public, private and protected (4)\
\
**Program Implementation (35)**\
*   Compiles, executes and runs without error (5)\
*   Execution (how well does the actual program work) (6)\
*   Completeness (how complete is the submitted program, how functional) (4)\
*   Modular, non-duplicated code (short, reusable, single-purpose methods) (4)\
*   Program documentation (commenting, formatting, suitable names of variables, etc) (4)\
*   Clarity and simplicity of program (good program structure, logical flow) (4)\
*   Extended functionality (going beyond the basic specification) (8)\
\
---\
Lecture 18: Assessment 2024/25\
18.7\
Software Design\
---\
\
## Assignment\
\
**Report (35)**\
*   Explanation of the flocking algorithm you used (5)\
*   Explanation of design (10)\
*   Explanation of program implementation (10)\
*   Summary of test procedures and results (5)\
*   Readability and formatting (5)\
\
---\
Lecture 18: Assessment 2024/25\
18.8\
Software Design\
---\
\
## Questions\
\
Can you ask questions?\
\
*   Yes, if it relates to a previous laboratory (or lecture, etc)\
*   Yes, if it relates to what the assessment is asking of you\
*   No (usually), if it relates to how to do the assessment\
    *   But, if unsure, please ask\
    *   Answering \'93no\'94 is easy\
\
---\
Lecture 18: Assessment 2024/25\
18.9\
Software Design\
---\
\
## The End!\
\
---\
Lecture 18: Assessment 2024/25\
18.10\
Software Design\
---\
}