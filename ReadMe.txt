to run:

1. javac BooleanRetrieval.java
2. java BooleanRetrieval

then to run query: 

Example runs:

$> project_executable PLIST cpu cpu_plist.txt
Should produce
cpu -> [604, 800, 959, 1156]
as the contents of cpu_plist.txt

$> project_executable AND mouse AND scrolling and_result.txt
Should produce
mouse AND scrolling -> [80, 86, 348, 1029]
as the contents of and_result.txt

$> project_executable AND-NOT Lenovo AND (NOT logitech) and_not_result.txt
Should produce
lenovo AND (NOT logitech) -> [360, 373, 379, 451, 517, 540, 787, 869, 942, 1055, 1146]
as the contents of and_not_result.txt