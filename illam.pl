#!/bin/perl -w
#This file is part of Illam project.

#Illam is free software: you can redistribute it and/or modify
#it under the terms of the GNU General Public License as published by
#the Free Software Foundation, either version 3 of the License, or
#(at your option) any later version.

#Illam is distributed in the hope that it will be useful,
#but WITHOUT ANY WARRANTY; without even the implied warranty of
#MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#GNU General Public License for more details.

#You should have received a copy of the GNU General Public License
#along with Illam.  If not, see <http://www.gnu.org/licenses/>.

use strict;
use Gedcom;
use Term::Menus;
use Getopt::Long;

if (@ARGV < 1) {
    print STDERR "Usage: perl illam.pl <name of database file>\n";
    exit 1;
}


#read gedcom file from user input  
my $ged = Gedcom->new(shift);
my $debug = 0;
GetOptions(
  'd:s'   => \$debug,
);  
system("clear");
&printMainMenu();
my %userchoice;
my %visitedList;    #keeps track of whether a node was visited or not



#
# mainMenu - User choices for the main menu
#
# @param - none
#
# @return - none
#
sub printMainMenu {
    print "[1]\tPrint database\n";
    print "[2]\tPrint illam names\n";
    print "[3]\tSearch database\n";
    print "[4]\tFind relations\n";
    print "[5]\tFind pula\n";
    print "[6]\tPrint family invites\n";
    print "[7]\tExit\n";
    
    print "\nEnter your choice:";
    my $choice=<STDIN>;
    if($choice == "1") { &printDatabase(); }
    if($choice == "2") { &printIllams(); }
    if($choice == "3") { &browseMenu(); }
    if($choice == "4") { &relationsMenu(); }
    if($choice == "5") { &pulaMenu(); }
    #if($choice == "6") { &familyMenu(); }
    if($choice == "7") { return; } else { &printMainMenu(); }
}


#
# pdebug - Print a debug message if debug level is turned ON
#
# @param - message
#
# @return - none
#
sub pdebug {
  my $msg=shift;
  if($debug==1) {
    print $msg;
  }
}


#
# browseMenu - User choices for the browse menu
#
# @param - none
#
# @return - none
#
sub browseMenu {
    my @results;
    my $id;
    my $selection;

    do {
        print "\nEnter name/ID (\"type \"x\" to select/return): ";
        $id=<STDIN>;
        chomp($id);
        if($id eq "x") {
            return $selection;
        }
        elsif($id=~/^[0-9]+$/) {
            @results=&printInfo($results[$id]);
            $selection=$results[$id];
        }
        else {
            @results=&searchPerson($id);
            if(@results==0) { 
                print "No record found matching name $id\n";
            }
            else {
                print("\nDid you mean ?\n\n");
                my $i=0;
                foreach(@results) {
                    print "\t[$i] ".&prettyName($_->name)."\n";
                    $i++;
                }
                print "\n"; 
            }
        }
    }while(($id ne "x"));
    
    return $selection;
}

#
# selectPerson - User choices for the select person menu
#
# @param - none
#
# @return - none
#
sub selectPerson {
    my @results;
    my $id;
    my $selection;

    print "Enter name (\"type \"x\" to select/return): ";
    $id=<STDIN>;
    chomp($id);
    if($id eq "x") {
        return $selection;
    }

    @results=&searchPerson($id);
    if(@results==0) { 
        print "No record found matching name $id\n";
        return "";
    }
    else {
        print("\nDid you mean ?\n\n");
        my $i=0;
        foreach(@results) {
            print "\t[$i] ".&prettyName($_->name)."\n";
            $i++;
        }
        print "\n"; 
    }

    $id=<STDIN>;
    if($id=~/^[0-9]+$/) {
        $selection=$results[$id];
        return $selection;
    }
    else {
        return "";  
    }
    
    return $selection;
}

#
# pulaMenu - User choices for the relations menu
#
# @param - none
#
# @return - none
#
sub relationsMenu {
    print "Select person 1: ";
    my $personA = &selectPerson();
    print "Person selected as ".&prettyName($personA->name)."\n";

    print "Select person 2: ";
    my $personB = &selectPerson();
    print "Person selected as ".&prettyName($personB->name)."\n";

    &printRelation($personA,$personB);
    %visitedList=();
    return;
}

#
# pulaMenu - User choices for the pula menu
#
# @param - none
#
# @return - none
#
sub pulaMenu {
    print "Calculate pula if the following person dies (Enter name): ";
    my $person = &selectPerson();
    print "You entered ".&prettyName($person->name)."\n";
    print "Pula applies for following members:\n";
    &pulaAtDeath($person);
    my @pulaList;
}

#
# printDatabase - Print the entire database
#
# @param - none
#
# @return - none
#
sub printDatabase {
  for my $i ($ged->individuals)
  {
    print &prettyName($i->name)."\n";
  }
  print "Database has ".&getTotalIndividuals()." members \n";
}

#
# printIllams - Filter and print illam names from the database
#
# @param - none
#
# @return - none
#
sub printIllams {
  my @illams;
  my %illam_men;
  my %illam_women;

  for my $i ($ged->individuals)
  {
    my $illam_name=&illam($i);
    if(!(/$illam_name/i~~@illams)) {
        push(@illams,$illam_name);
    }
    if($i->sex eq "M") { $illam_men{$illam_name}=$illam_men{$illam_name}+1; }
    if($i->sex eq "F") { $illam_women{$illam_name}=$illam_women{$illam_name}+1; }
    
  }

  @illams=sort(@illams); 
  foreach(@illams) { 
    my $total=$illam_men{$_}+$illam_women{$_};
    print "$_\t\t[Total=$total, $illam_men{$_} purushan, $illam_women{$_} sthree]\n";
  }
}

#
# getTotalIndividuals - Return the total number of records in the database
#
# @param - none
#
# @return - total number of individuals
#
sub getTotalIndividuals {
    return $ged->individuals;
}

#
# searchPerson - Return a set of records that match a person's record
#
# @param - a person record
#
# @return - none
#
sub searchPerson {
  my @matches = $ged->get_individual($_[0]);
  return @matches;
}

#
# prettyName - Nicely print a person's name
#
# @param - a person record
#
# @return - none
#
sub prettyName {
  my @names = split("/",$_[0]);
  my $given = $names[0];
  my $sur = $names[1];
  $given=~s/Unknown//g; #ignore unknown father's name
  return "$sur $given";
}

#
# illam - Return a person's illam
#
# @param - a person record
#
# @return - person's illam
#
sub illam {
  my @names = split("/",$_[0]->name);
  my $sur = $names[1];
  return $sur;
}

#
# ammath - Return mother's illam
#
# @param - a person record
#
# @return - mother's illam
#
sub ammath {
  if($_[0]->mother) {
    return &illam($_[0]->mother);
  }
  else {
    return "Unknown\n";
  } 
}

#
# hasFather - Return true if a record exists against person's father entry
#
# @param - a person record
#
# @return - 1 if father's record exists, 0 otherwise
#
sub hasFather {
    return ($_[0]->father)?1:0;
}

#
# hasSpouse - Return true if a record exists against person's mother entry
#
# @param - a person record
#
# @return - 1 if mother's record exists, 0 otherwise
#
sub hasMother {
    return ($_[0]->mother)?1:0;
}

#
# hasSpouse - Return true if the person is married
#
# @param - a person record
#
# @return - 1 if the person is married, 0 otherwise
#
sub hasSpouse {
    return ($_[0]->spouse)?1:0;
}

#
# hasSons - Return true if the person has sons
#
# @param - a person record
#
# @return - 1 if person has sons
#
sub hasSons {
    return ($_[0]->sons)?1:0;
}

#
# hasDaughters - Return true if a person has daughters
#
# @param - a person record
#
# @return - 1 if person has daughters
#
sub hasDaughters {
    return ($_[0]->daughters)?1:0;
}

#
# hasChildren - Return true if a person has sons/daughters
#
# @param - a person record
#
# @return - 1 if person has children
#
sub hasChildren {
    return (&hasSons($_[0])||&hasDaughters($_[0]));
}

#
# printInfo - Prints info about a person record
#
# @param - a person record
#
# @return - none
#
sub printInfo {
    
    my $person = $_[0];
    my @relations;
    
    print "\n*****************************************************\n\n";
    my $index=0;
    print "Name:\t\t[".$index++."] ".&prettyName($person->name)."\n";
    push(@relations,$person);
    
    if(&hasFather($person)) {   
        print "Achan:\t\t[".$index++."] ".&prettyName($person->father->name)."\n";
        push(@relations,$person->father);
    }

    if(&hasMother($person)) {
        print "Amma:\t\t[".$index++."] ".&prettyName($person->mother->name)."\n";
        push(@relations,$person->mother);
    }

    if(&hasSpouse($person)) {
        my @spouses = $person->spouse;
        print "Spouses:\n";
        foreach (@spouses) {
            print "\t\t[".$index++."] ".&prettyName($_->name)."\n";
            push(@relations,$_);
        }
    }

    if(&hasSons($person)) {
        my @sons = $person->sons;
        print "Sons:\n";
        foreach (@sons) {
            print "\t\t[".$index++."] ".&prettyName($_->name)."\n";
            push(@relations,$_);

        }
    }

    if(&hasDaughters($person)) {
        my @daughters = $person->daughters;
        print "Daughters:\n";
        foreach (@daughters) {
            print "\t\t[".$index++."] ".&prettyName($_->name)."\n";
            push(@relations,$_);
        }
    }

    print "\n\nIllam:\t\t".&illam($person)."\n";
    if(&hasMother($person)) {
        print "Ammath:\t\t".&ammath($person)."\n";
    } else {
        print "Ammath:\t\tariyilya\n";
    }

    if(&hasFather($person)) {   
        if(&hasMother($person->father)) {
            print "Achande ammath:\t".&illam($person->father->mother)."\n";
        } else {
            print "Achande ammath:\t\tariyilya\n";
        }
     } else {
        print "Achande ammath:\t\tariyilya\n";
     }

    if(&hasMother($person)) {
        if(&hasMother($person->mother)) {
            print "Ammede ammath:\t".&illam($person->mother->mother)."\n";
        } else {
            print "Ammede ammath:\tariyilya\n";
        }
    } else {
        print "Ammede ammath:\tariyilya\n";
    }

    # Illathe information
    if(&hasFather($person)) {   
        if(&hasFather($person->father)) {
            if(&hasFather($person->father->mother)) {
                print "Illathe muthashante ammath:\t".&illam($person->father->father->mother)."\n";         
            } else {
                print "Illathe muthashante ammath:\tariyilya\n";            
            }
        } else {
            print "Illathe muthashante ammath:\tariyilya\n";            
        }
    } else {
        print "Illathe muthashante ammath:\tariyilya\n";            
    }

    if(&hasFather($person)) {   
        if(&hasMother($person->father)) {
            if(&hasMother($person->father->mother)) {
                print "Illathe muthasheede ammath:\t".&illam($person->father->mother->mother)."\n";         
            } else {
                print "Illathe muthasheede ammath:\tariyilya\n";            
            }
        } else {
            print "Illathe muthasheede ammath:\tariyilya\n";            
        }
    } else {
        print "Illathe muthasheede ammath:\tariyilya\n";            
    }


    # Ammathe information
    if(&hasMother($person)) {   
        if(&hasFather($person->mother)) {
            if(&hasMother($person->mother->father)) {
                print "Ammathe muthashante ammath:\t".&illam($person->mother->father->mother)."\n";         
            } else {
                print "Ammathe muthashante ammath:\tariyilya\n";            
            }
        } else {
            print "Ammathe muthashante ammath:\tariyilya\n";            
        }
    } else {
        print "Ammathe muthashante ammath:\tariyilya\n";            
    }

    if(&hasMother($person)) {   
        if(&hasMother($person->mother)) {
            if(&hasMother($person->mother->mother)) {
                print "Ammathe muthasheede ammath:\t".&illam($person->mother->mother->mother)."\n";         
            } else {
                print "Ammathe muthasheede ammath:\tariyilya\n";            
            }
        } else {
            print "Ammathe muthasheede ammath:\tariyilya\n";            
        }
    } else {
        print "Ammathe muthasheede ammath:\tariyilya\n";            
    }
#   print "Ancestors\n";
#   my @ancestors = $_[0]->ancestors;
#   foreach (@ancestors) {
#       print &prettyName($_->name)."\n";
#   }
    #&printRelation($person,$person->mother->father);
    
    print "\n*****************************************************\n";
    return @relations;
}


#
# isVisited - Checks if the person record was traversed during relation-ship search
#
# @param - none
#
# @return - 1 if the record was visited, 0 otherwise
#
sub isVisited {
    if($visitedList{$_[0]} eq "v") {
        return 1;   
    }
    else {
        return 0;
    }
}

#
# printName - Print the name of a person from the record
#
# @param - a person record
#
# @return - none
#
sub printName {
    my $person=shift; 
    print &prettyName($person->name);
}


#
# printRelation - Accepts two person records A and B, prints how person B is related to person A
#
# @param 1 - a person record A
# @param 2 - a person record B
#
# @return - none
#
sub printRelation {
    my ($personA,$personB) = @_;
    if ($personA==$personB) {
        print "Please enter different names!\n";
        return;
    }

    # We will use a breadth-first-search algorithm to explore the family tree starting from record B.
    # If record A was found during the search process, stop. We will then back-track and print the relation
    # Otherwise, we throw a message that says the relation was not found.
    # a breadth-first-search strategy is used to find the closest relation.
    # try to search in father, mother, all sons, all daughter, spouse's relations, mark visited
    my @searchQ;
    my %predecessor;   #keeps track of our route
    my $found=0;

    push(@searchQ,$personA);
    $predecessor{$personA}=$personA;
    
    # as we traverse the graph, we mark at each node the previous node from which we reached this node
    # we store this predecssor information in the hash-table %predecessor
    while(@searchQ>0) {
        my $n = shift(@searchQ);
        #print "Processing ".$n->name." visited list status is ".$visitedList{$n}."\n";
        
        if(!(&isVisited($n))) { 
            $visitedList{$n}="v";
            #$prevNode = $n;    

            #print "Visiting ".&prettyName($n->name)."\n";
            if(&prettyName($n->name) eq &prettyName($personB->name)) {
                $found=1;
                last;
            } 
        
            if(&hasFather($n)) {    
                if(!(&isVisited($n->father))) { 
                    push(@searchQ,$n->father);
                    #print "Adding ".$n->father->name." prev is ".$n->name."\n";
                    #if(!$predecessor{$n->father}) { 
                        $predecessor{$n->father}=$n; 
                    #}
                }  
            }

            if(&hasMother($n)) {    
                if(!(&isVisited($n->mother))) { 
                    push(@searchQ,$n->mother); 
                    #print "Adding ".$n->mother->name." prev is ".$n->name."\n";    
                    #if(!$predecessor{$n->mother}) { 
                        $predecessor{$n->mother}=$n; 
                    #}
                }
            }

            if(&hasSpouse($n)) {
                foreach($n->spouse) { 
                    if(!(&isVisited($_))) { 
                        push(@searchQ,$_); 
                        #print "Adding ".$n->spouse->name." prev is ".$n->name."\n";    
                        #if(!$predecessor{$_}) { 
                            $predecessor{$_}=$n; 
                        #}
                    }
                }
            }
    
            if(&hasSons($n)) {
                foreach($n->sons) {
                    if(!(&isVisited($_))) { 
                        push(@searchQ,$_);
                        #print "Adding ".$_->name." prev is ".$n->name."\n";
                        #if(!$predecessor{$_}) { 
                            $predecessor{$_}=$n; 
                        #}
                    }
                }
            }

            if(&hasDaughters($n)) {
                foreach($n->daughters) {
                    if(!(&isVisited($_))) { 
                        push(@searchQ,$_);
                        #print "Adding ".$_->name." prev is ".$n->name."\n";
                        if(!$predecessor{$_}) { 
                            $predecessor{$_}=$n; 
                        }
                    }
                }
            }
        }
    }
   
    my @relationStack;
    # if we reached the second person, we have found a relation
    if($visitedList{$personB} eq "v") {        
        print "Relation found!\n";

        # build a simple relation stack by back-tracking predecessor info
        my $n = $personB;
        while($n != $personA) {
            my $pred = $predecessor{$n};
            my $relation = &defineSimpleRelation($pred,$n);
            push(@relationStack,&defineSimpleRelation($pred,$n));
            $n = $pred;
        }

        pdebug("Relation stack is ");
        foreach(@relationStack) {
            pdebug("$_,");
        }
        # convert the simple relation stack into customized relation names
        my @customRelationStack;
        while (@relationStack>0) {
            my $simpleRelation = pop(@relationStack);
            if(@customRelationStack==0) {
                push(@customRelationStack,$simpleRelation);
            } else {
                my $top = pop(@customRelationStack);
                my $customRelation = &defineCustomRelation($top,$simpleRelation);
                if ($customRelation eq "none" ) {
                    push(@customRelationStack,$top);
                    push(@customRelationStack,$simpleRelation);
                } else {
                    push(@customRelationStack,$customRelation);
                }
            }
        }

        # improved display
        if ($personA->sex eq "M") {
            print &prettyName($personA->name)."'nde";
        } else {
            print &prettyName($personA->name)."'ede";
        }
       
        # print the relation here
        while(@customRelationStack>0) {
            my $string = shift(@customRelationStack);
            if(@customRelationStack==0) {
                print " $string anu ".&prettyName($personB->name)."\n\n";
            }
            else {
                print " $string"."de";
            }
        }
    } else {
        print "Relation not found\n";
    }

}

#
# Define simple relation-ship names
#
# @param 1 - a person record A
# @param 2 - a person record B
#
# @return - a simple relation that says "person A is person B's ________"
#
sub defineSimpleRelation {
    my ($record_a,$record_b) = @_;
    
    # achan = persons's father	
    if(&hasFather($record_a)) {
        if($record_a->father==$record_b) { 
            return "achan"; 
        }
    }

    # amma = person's mother
    if(&hasMother($record_a)) {
        if($record_a->mother==$record_b) {
            return "amma"; 
        }
    }

    # husband/wife 
    if(&hasSpouse($record_a)) {
        foreach($record_a->spouse) { 
            if(($_==$record_b) && ($record_b->sex eq "F")) {
                return "wife";
            } 
            if(($_==$record_b) && ($record_b->sex eq "M")) {
                return "husband";
            }
        }
    }
    
    # makan = person's son
    if(&hasSons($record_a)) {
        foreach($record_a->sons) {
            if($_==$record_b) {
                return "makan";
            }
        }   
    }

    # makal = person's daughter
    if(&hasDaughters($record_a)) {
        foreach($record_a->daughters) {
            if($_==$record_b) {
                return "makal";
            }
        }   
    }

    # default
    return "error";
}


sub pulaAtDeath {

    my $person = $_[0];

    # Rule 1 Pula for Parents
    print $person->father->name." \n";
    foreach($person->father->spouse) {
        print $_->name." \n";
        foreach($_->sons) {
            if($person != $_) {
                print $_->name." \n";
            }
        }
        foreach($_->daughters) {
            if($person != $_) {
                print $_->name." \n";
            }
        }
    }
    
}   
    

#
# Define custom relation-ship names
#
# @param 1 - a custom relation (A)
# @param 2 - a simple relation (B)
#
# @return - a custom relation that tells what is A's B usually called as ? 
#           for e.g. amma's achan is called ammathe muthashan
#
sub defineCustomRelation {
    my $custom = $_[0];
    my $simple = $_[1];

    &pdebug("Custom = $custom Simple=$simple\n");
    
    if ($custom eq  "achan" && $simple eq "wife") {
        return "amma"; 
    }
    elsif ($custom eq  "amma" && $simple eq "husband") {
        return "achan"; 
    }
    elsif ($custom eq  "achan" && $simple eq "makan") {
        return "brother"; 
    }
    elsif ($custom eq  "amma" && $simple eq "makan") {
        return "brother"; 
    }
    elsif ($custom eq  "achan" && $simple eq "makal") {
        return "sister"; 
    }
    elsif ($custom eq  "amma" && $simple eq "makal") {
        return "sister"; 
    }
    elsif ($custom eq  "amma" && $simple eq "achan") {
        return "ammathe muthashan"; 
    }
    elsif ($custom eq  "amma" && $simple eq "amma") {
        return "ammathe muthashi"; 
    }
    elsif ($custom eq  "achan" && $simple eq "achan") {
        return "illathe muthashan"; 
    }
    elsif ($custom eq  "achan" && $simple eq "amma") {
        return "illathe muthashi"; 
    }
    elsif ((($custom eq  "ammathe muthashan")||($custom eq "ammathe muthashi")) && $simple eq "makan") {
        return "ammaman"; 
    }
    elsif ((($custom eq  "ammathe muthashan")||($custom eq "ammathe muthashi")) && $simple eq "makal") {
        return "perashi/shittashi"; 
    }
    elsif ((($custom eq  "illathe muthashan")||($custom eq "illathe muthashi")) && $simple eq "makan") {
        return "abhan/valyachan"; 
    }
    elsif ((($custom eq  "illathe muthashan")||($custom eq "illathe muthashi")) && $simple eq "makal") {
        return "achammal"; 
    }
    elsif ($custom eq  "ammaman" && $simple eq "wife") {
        return "ammayi"; 
    }
    elsif ($custom eq  "ammayi" && $simple eq "husband") {
        return "ammaman"; 
    }
    elsif ($custom eq  "ammathe muthashan" && $simple eq "brother") {
        return "ammathe muthabhan"; 
    }
    elsif ($custom eq  "illathe muthashan" && $simple eq "brother") {
        return "illathe muthabhan"; 
    }
    #elsif ($custom eq  "makal" && $simple eq "makal") {
    #    return "perakutty"; 
    #}
    #elsif ($custom eq  "makan" && $simple eq "makan") {
    #    return "perakutty"; 
    #} 
    elsif ($custom eq  "wife" && $simple eq "makan") {
        return "makan"; 
    }
    elsif ($custom eq  "wife" && $simple eq "makal") {
        return "makal"; 
    } 
    elsif ($custom eq  "husband" && $simple eq "makan") {
        return "makan"; 
    }
    elsif ($custom eq  "husband" && $simple eq "makal") {
        return "makal"; 
    } else {
        return "none";
    }
}

