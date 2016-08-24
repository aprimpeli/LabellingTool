The labeling tool consists of two parts. The user can run the two parts separately using the main 
functions of the classes EntitiesInspectorNQuads and FeatureLabelinginHTML or all together by using
the main method of the Initializer class. 

Extended Documentation of the Labeling Tool:

Part 1  - Finding product names in .nq files

1. The user needs to give the path of the nq and warc files. Please note that the tool considers that 
the nq and warc files retrieved from a specific pld will both be named after it. In this step 
validation of the input is done in order to realize if the given paths exist or not.

2. Then the user is asked to give the type of products to be searched in the .nq files. As the tool
is based three product categories, namely television, laptop and mobile phones the user needs to 
type one of those three words or the word all in case they want all of the product names to be searched.
Validation of input is done in this step.

3.Based on the input of the previous step an arraylist containing all the products to be searched  will
be created. The .nq files of the defined folder will be then transformed into entities (using the 
EntityProcessor class of the webdatacommons project). 

4. For every created entity we are interested only on those that have schema.org/Product/description
and/or schema.org/Product/title as predicates. The objects of those predicates are lowercased, tokenized
and put in an arraylist.

5. Iterate for every product name, transform it, tokenize it and put it into an arraylist.

6. The containment of the product arraylist to the description and title arraylists is calculated. We 
only consider full containment.

7. If the product name is found in an object of a created entity then an entry is created into the 
NQuadsProductResults.txt file which includes the following information:
<node>|<productname>|<url>|<pld>|<gtin13:value>|<gtin14:value>|<title:title>|<description:description>|<lang>|<currentNQFile>. 
This step includes also language detection for the title and description values.

Part 2 - Labeling product specification items from html pages found in warc files

8. For every entry of the output file of part 1 (NQuadsProductResults.txt) the pld is retrieved and
the warc file with the same name is searched in the directories of warcs.

9. For the chosen warc file we look for the warc response that is given by the current url and the html 
response is then copied in a temporary file under the directory resources/tempHTML.html

10. The user needs to specify (yes/no) if the html file contains product specifications. If no the next 
product node-url will be considered.

11. The user needs to specify if the page includes specifications for a mobile_phone, laptop or 
television by typing the relevant word. Input validation is done.

12. The title will then be printed and the user is asked if they want to label the title (yes/no).
If no the tool will pass over to the next part of information, ie:description.

13. If yes, the title will be tokenized and every token will appear for the user to give the correct 
label. If there is a token that the user does not with to label they can simply type next.

14. By the end of the labeling of the title the user will be asked to define the mappings between 
the new labels they gave and the predefined ones. The calculation of which given labels do not belong 
to the list of the predefined ones is done automatically. So the undefined labels are printed in the 
console and the user needs to specify the predefined attribute of the current product type that best
matches the given label.

15. The steps 12-14 are repeated in an identical way for the description as well.

16. The user is asked if they want to continue with labeling the tables of the page. An iteration
in every table contained in the html page will be done, the table is printed and the user is asked
if it is a specification table or not. If not the iteration moves on to the next table./ As it is 
more time effective, the user can directly define in the console the feature value pairs from the 
tables without iterating and printing the tables. In that case steps 17,18 are not executed.

17. For a specification table the user is asked to type tr if the feature names are found in the
rows of the table and td if the feature names are found in the columns of the table.

18. The feature and the values will be extracted in the structure <feature>:<value> and the user 
is asked if this should be considered a right labeling or not. If not the user needs to give an 
alternative one for the current feature value pair.

19. As in step 14 all the newly defined labels will be asked to be mapped by the user.

20. Steps 17-19 are repeated similarly for lists.
21. Finally a JSON object for all the provided labelings is created that contains the wished
structure and written in the output file JSON_LabelingOutput.txt.

