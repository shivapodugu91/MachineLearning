Language Used : R

IDE used : RStudio

Checks:
installed.packages()
Please use above method to check if packages are already installed.

Packages Used:

library(caret)
library(MASS)
library(class)
library(e1071)
library(readxl)
library(randomForest)
library(maboost)
library(ipred)
library(readxl)
library(rpart)
library(gbm)
library(adabag)
library(dplyr)
library(neuralnet)

While installing the packages, please download all the dependency packages associated with them.

library download script:

install.packages("caret")
install.packages("MASS")
install.packages("class")
install.packages("e1071")
install.packages("readxl")
install.packages("randomForest")
install.packages("maboost")
install.packages("ipred")
install.packages("readxl")
install.packages("rpart")
install.packages("gbm")
install.packages("adabag")
install.packages("dplyr")
install.packages("neuralnet")

Execute:

Attached two files namely Testing_all_classifiers_together(R_Code),Pseudo_codes_individualclassifiers(R_code)

Testing_all_classifiers_together(R_Code): 

Copy the script and paste it in the RStudio.
In the script, we have hardcoded the global variable path.
for example: path <<-"~/Desktop/ML/Assignment/Assignment5/iris_raw_dataset.xls"
so please replace the path with the path where you copied the iris_raw_dataset.xls file.
this program outputs the average accuracy and precision of all the classifiers after 10 folds.

Pseudo_codes_individualclassifiers(R_code):

This folder contains the all individual classifiers r code.
Copy the script and paste it in the RStudio.
In the script, we have hardcoded the path in read_excel method
for example: read_excel("~/Assignments/MI/Assignment5/iris_raw_dataset.xls")
so please replace the path with the path where you copied the iris_raw_dataset.xls file.
Each program outputs the average accuracy and precision of the respective classifier after 10 folds.
