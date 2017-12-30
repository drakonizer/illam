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
use DateTime;
use Getopt::Long qw(GetOptions);
use Pod::Usage qw(pod2usage);

# VERSION

=head1 SYNOPSIS

    illam.pl [--file <path to gedcom file>] [--debug] [--test] [--sql]

=head1 DESCRIPTION
     Options:
       -file            path to gedcom file
       -debug           enable debug message
...

=cut
my $file;
my $debug;
my $test;
GetOptions(
    q(help)             => \my $help,
    q(verbose)          => \my $verbose,
    "file=s"            => \$file,
    "debug=s"           => \$debug,
    "test=s"            => \$test,
    q(debug)            => \my $debug,
    q(test)             => \my $test,
    q(sql)              => \my $sql
) or pod2usage(q(-verbose) => 1);
pod2usage(q(-verbose) => 1) if $help;
pod2usage(q(-verbose) => 1) if ($file eq "");

#read gedcom file from user input  
my $ged = Gedcom->new($file);
my @persons = $ged->individuals;

if($test ne "") {
   unitTest();
   exit 0;
}

if($sql ne "") {
   genSql();
   exit 0;
}

system("clear");
&printMainMenu();


my %userchoice;
my %visitedList;    #keeps track of whether a node was visited or not


#
# genSql - Generate SQL database
#
# @param - none
#
# @return - none
#
sub genSql {
    my $filename = 'names.sql';
	my $cnt = 0;
    open(my $fh, '>', $filename) or die "Could not open file '$filename' $!";
    print $fh "CREATE TABLE table1 (_id integer PRIMARY KEY, name text, father text, mother text);\n";
	print $fh "CREATE TABLE \"android_metadata\" (\"locale\" TEXT DEFAULT 'en_US');\n";
    print $fh "INSERT INTO \"android_metadata\" VALUES ('en_US');\n";

	foreach my $spouse (0..3) {
		print $fh "ALTER TABLE table1 ADD spouse_$spouse text;\n"
	}
	
	foreach my $child (0..15) {
		print $fh "ALTER TABLE table1 ADD child_$child text;\n"
	}
	
	print $fh "ALTER TABLE table1 ADD sex text;\n";

	for my $i ($ged->individuals)
	{
    	print $fh "INSERT INTO table1 (_id) VALUES ($cnt);\n";
		print $fh "UPDATE table1 SET name = '".&prettyName($i->name)."' WHERE _id = $cnt;\n";
		if(&hasFather($i)) {
			print $fh "UPDATE table1 SET father = '".&prettyName($i->father->name)."' WHERE _id = $cnt;\n";
		} 

		if(&hasMother($i)) {
			print $fh "UPDATE table1 SET mother = '".&prettyName($i->mother->name)."' WHERE _id = $cnt;\n";
		} 

        my @spouses = $i->spouse;
        my $spouse_cnt = 0;
        foreach (@spouses) {
			print $fh "UPDATE table1 SET spouse_$spouse_cnt = '".&prettyName($_->name)."' WHERE _id = $cnt;\n";
			$spouse_cnt++;
		}

        my @children = $i->children;
        my $child_cnt = 0;
        foreach (@children) {
			print $fh "UPDATE table1 SET child_$child_cnt = '".&prettyName($_->name)."' WHERE _id = $cnt;\n";
			$child_cnt++;
		}

		if ($i->sex eq "M") {
			print $fh "UPDATE table1 SET sex = \"male\" WHERE _id = $cnt;\n";
		} else {
			print $fh "UPDATE table1 SET sex = \"female\" WHERE _id = $cnt;\n";
		}
		$cnt++;

	}
    close $fh;
}

#
# unitTest - Run unit tests
#
# @param - none
#
# @return - none
#
sub unitTest {
    if(open(my $fh, "test.txt")) {
        while (my $row = <$fh>) {
            $row =~ /^$/ and die "Blank line detected at $. Exiting.\n";
            if($row =~ /^#/) {
                next;
            }
            chomp $row;            
            my @test_vec = split /\|/, $row;
            my $personA = $test_vec[0];
            my $personB = $test_vec[1];
            #clean-up beginning and trailing spaces
            $personA =~ s/^\s+|\s+$//g;
            $personB =~ s/^\s+|\s+$//g;
            my @exp_relations = split /,/, $test_vec[2];
            
            my @relationStack = printRelation(searchPerson(firstMatch($personA)),searchPerson(firstMatch($personB)));

            if(@relationStack == 0) {
                print "Test failed\n";
                return;
            }

            while(@relationStack > 0) {
               my $exp = shift @exp_relations;
               my $rel_ref = shift @relationStack;
               pdebug("rel = $exp rel_ref is ".$rel_ref->{'relation'});
               if( $exp ne $rel_ref->{'relation'}) {
                    print "Test failed\n";
                    return;
                }
            }
            %visitedList=();
        }
        print "All tests passed!\n";
    } else {
        warn "Could not open test file\n";        
    } 
    return;    
}

#
# firstMatch - Return the first match for the requested name
#
# @param - name string
#
# @return - person id
#
sub firstMatch {
    my @names = searchPerson(shift);
    pdebug("Pretty name = ".prettyName($names[0]->name));
    return $names[0]->name;
}

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
    #print "is older ".isAOlderThanB("01 JAN 1990", "14 JAN 1984")."\n";

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
                return $selection;
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

    print "Test Enter name (\"type \"x\" to select/return): ";
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
# relationsMenu - User choices for the relations menu
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
    print "Name of the person: ";
    my $person = &selectPerson();
    print "You entered ".&prettyName($person->name)."\n";
    print "Pula applies for :\n";
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
# hasBrothers - Return true if a person has brothers
#
# @param - a person record
#
# @return - 1 if person has brothers
#
sub hasBrothers {
    return ($_[0]->brothers)?1:0;
}

# hasSisters - Return true if a person has sisters
#
# @param - a person record
#
# @return - 1 if person has sisters
#
sub hasSisters {
    return ($_[0]->sisters)?1:0;
}

# isYounger - Compares age of 2 person A and B
#
# @param - person A and person B
#
# @return - 1 if person A older than B, 0 otherwise
#
sub isAOlderThanB {
    my ($dob_a, $dob_b) = @_;
    my @doba = split(" ",$dob_a);
    my @dobb = split(" ",$dob_b);
    my $year1 = $doba[2];
    my $year2 = $dobb[2];
    my $dt1 = DateTime->new(year => $year1);
    my $dt2 = DateTime->new(year => $year2);

    DateTime->compare($dt1, $dt2);
    
    if($dt1 < $dt2) {
        return 1;
    }
    else {
        return 0;
    }
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
# @return - relationStack
#
sub printRelation {
    my @customRelationStack;
    my @ret_relationStack;
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
                pdebug("Found\n");
                $found=1;
                last;
            } 
         
            if(&hasBrothers($n)) {
                foreach($n->brothers) {
                    if(!(&isVisited($_))) { 
                        push(@searchQ,$_);
                        #print "Adding ".$_->name." prev is ".$n->name."\n";
                        if(!$predecessor{$_}) { 
                            $predecessor{$_}=$n; 
                        }
                    }
                }
            }

            if(&hasSisters($n)) {
                foreach($n->sisters) {
                    if(!(&isVisited($_))) { 
                        push(@searchQ,$_);
                        #print "Adding ".$_->name." prev is ".$n->name."\n";
                        if(!$predecessor{$_}) { 
                            $predecessor{$_}=$n; 
                        }
                    }
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
        pdebug("Relation found!\n");

        # build a simple relation stack by back-tracking predecessor info
        my $n = $personB;
        while($n != $personA) {
            my $pred = $predecessor{$n};
            my $rel_ref = &defineSimpleRelation($pred,$n);
            push(@relationStack,$rel_ref);
            $n = $pred;
        }

        # convert the simple relation stack into customized relation names        
        while (@relationStack>0) {
            my $simpleRelation_ref = pop(@relationStack);
            if(@customRelationStack == 0) {
                push(@customRelationStack,$simpleRelation_ref);
            } else {
                my $top = pop(@customRelationStack);
                my $customRelation_ref = &defineCustomRelation($top,$simpleRelation_ref);
                if ($customRelation_ref->{'relation'} eq "none") {
                    push(@customRelationStack,$top);
                    push(@customRelationStack,$simpleRelation_ref);
                } else {                    
                    push(@customRelationStack,$customRelation_ref);
                }
            }
        }

        # do an additional pass
        my @relationStack = reverse @customRelationStack;
        #print "@relationStack";

        undef @customRelationStack;
        while (@relationStack>0) {
            my $simpleRelation_ref = pop(@relationStack);
            if(@customRelationStack == 0) {
                push(@customRelationStack,$simpleRelation_ref);
            } else {
                my $top = pop(@customRelationStack);
                my $customRelation_ref = &defineCustomRelation($top,$simpleRelation_ref);
                if ($customRelation_ref->{'relation'} eq "none") {
                    push(@customRelationStack,$top);
                    push(@customRelationStack,$simpleRelation_ref);
                } else {                    
                    push(@customRelationStack,$customRelation_ref);
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
        @ret_relationStack = @customRelationStack;
        my $string;
        while(@customRelationStack>0) {
            my $rel_ref = shift(@customRelationStack);
            $string = $rel_ref->{'relation'};
            
            if(exists($rel_ref->{'thavazhi'})) {
                $string = $string." (".prettyName($rel_ref->{'thavazhi'}->name)."thavazhi)"; 
            }
            
            if(@customRelationStack==0) {
                print " $string anu ".&prettyName($personB->name)."\n\n";
            }
            else {
                print " $string"."de";
            }
        }
    } else {
        print "Relation not found\n";
        undef @ret_relationStack;
    }
    return @ret_relationStack;

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
    my %relation = ();

    # achan = persons's father	
    if(&hasFather($record_a)) {
        if($record_a->father==$record_b) { 
            $relation{'relation'} = "achan";
            $relation{'record'} = $record_b;            
            return \%relation;
        }
    }

    # amma = person's mother
    if(&hasMother($record_a)) {
        if($record_a->mother==$record_b) {
            $relation{'relation'} = "amma";
            $relation{'record'} = $record_b;
            return \%relation;
        }
    }

    # husband/wife 
    if(&hasSpouse($record_a)) {
        my @spouses = $record_a->spouse;
        
        foreach($record_a->spouse) { 
            if(($_==$record_b) && ($record_b->sex eq "F")) {
                $relation{'relation'} = "wife";
                $relation{'record'} = $record_b;
                if(scalar @spouses > 1) {
                    pdebug("Multiple spouses for ".prettyName($record_a->name));
                    $relation{'thavazhi'} = $record_b;
                }                
                return \%relation;
            } 
            if(($_==$record_b) && ($record_b->sex eq "M")) {
                $relation{'relation'} = "husband";
                $relation{'record'} = $record_b;
                return \%relation;
            }
        }
    }
    
    # makan = person's son
    if(&hasSons($record_a)) {
        foreach($record_a->sons) {
            if($_==$record_b) {
                $relation{'relation'} = "makan";
                $relation{'record'} = $record_b;
                return \%relation;
            }
        }   
    }

    # makal = person's daughter
    if(&hasDaughters($record_a)) {
        foreach($record_a->daughters) {
            if($_==$record_b) {
                $relation{'relation'} = "makal";
                $relation{'record'} = $record_b;
                return \%relation;
            }
        }   
    }

    if(&hasBrothers($record_a)) {
        foreach($record_a->brothers) {
            if($_==$record_b) {
                my $doba = $record_a->get_value("birth date");
                my $dobb = $record_b->get_value("birth date");
                if(defined($doba) && defined($dobb)) {
                    if(isAOlderThanB($doba, $dobb)) {
                        $relation{'relation'} = "aniyan";
                        $relation{'record'} = $record_b;
                        return \%relation;
                    } else {
                        $relation{'relation'} = "ettan";
                        $relation{'record'} = $record_b;
                        return \%relation;
                    }
                } else {
                    $relation{'relation'} = "brother";
                    $relation{'record'} = $record_b;
                    return \%relation;
                }
            }
        }
    }

    if(&hasSisters($record_a)) {        
        foreach($record_a->sisters) {
            if($_==$record_b) {
                my $doba = $record_a->get_value("birth date");
                my $dobb = $record_b->get_value("birth date");
                if(defined($doba) && defined($dobb)) {
                    if(isAOlderThanB($doba, $dobb)) {
                        $relation{'relation'} = "aniyathi";
                        $relation{'record'} = $record_b;
                        return \%relation;
                    } else {
                        $relation{'relation'} = "chechi";
                        $relation{'record'} = $record_b;                                            
                        return \%relation;
                    }
                } else {
                    $relation{'relation'} = "sister";
                    $relation{'record'} = $record_b;
                    return \%relation;
                }
            }
        }
    }

    print "Returning error for ".&prettyName($record_a->name)." and ".&prettyName($record_b->name)."\n";
    # default
    return \%relation;
}


sub pulaAtDeath {

    my $person = $_[0];

    # Rule 1 Pula for Parents
    if(hasFather($person)) {
        prettyName($person->father);        
    }

    if(hasMother($person)) {
        prettyName($person->mother);
    }

    if(hasBrothers($person)) {
        foreach my $brother ($person->brothers) {
            prettyName($brother);
        }
    }

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
    my $custom_ref = $_[0];
    my $simple_ref = $_[1];
    my %relation = ();

    my $custom = $custom_ref->{'relation'};
    my $simple = $simple_ref->{'relation'};
 
    if ($custom eq  "achan" && $simple eq "wife") {
        $relation{'relation'} = "amma";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($simple_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $simple_ref->{'thavazhi'};
        }
        return \%relation;
    }

    if ($custom eq  "amma" && $simple eq "husband") {
        $relation{'relation'} = "achan";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;
    }

    if ($custom eq  "wife" && $simple eq "makan") {
        $relation{'relation'} = "makan";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }
        return \%relation;
    }
    if ($custom eq  "wife" && $simple eq "makal") {
        $relation{'relation'} = "makal";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }
        return \%relation;
    }

    if ($custom eq  "amma" && $simple eq "makan") {
        $relation{'relation'} = "brother";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }        
        return \%relation;
    }

    if ($custom eq  "husband" && $simple eq "makan") {
        $relation{'relation'} = "makan";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation; 
    }

    if ($custom eq  "husband" && $simple eq "makal") {
        $relation{'relation'} = "sister";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;        
    }

    if (($custom eq  "sister"  || 
        $custom  eq  "chechi"  || 
        $custom  eq  "aniyathi"||
        $custom  eq  "brother" ||
        $custom  eq  "ettan"   ||
        $custom  eq  "aniyan"  
    ) && $simple eq "amma") {
        $relation{'relation'} = "amma";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;        
    }

    if (($custom eq  "sister"  || 
        $custom  eq  "chechi"  || 
        $custom  eq  "aniyathi"||
        $custom  eq  "brother" ||
        $custom  eq  "ettan"   ||
        $custom  eq  "aniyan"  
    ) && $simple eq "achan") {        
        $relation{'relation'} = "achan";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;
    }

    if( $custom eq "achan" && $simple eq "chechi") {
        $relation{'relation'} = "illathe perassi";
        $relation{'record'} = $simple_ref->{'record'};        
        return \%relation;        
    }

    if( $custom eq "achan" && $simple eq "aniyathi") {
        $relation{'relation'} = "achammal";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;                        
    }

    if( $custom eq "achan" && $simple eq "ettan") {
        $relation{'relation'} = "valyachan";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;        
    }

    if( $custom eq "achan" && $simple eq "aniyan") {
        $relation{'relation'} = "abhan";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;        
    }

    if ($custom eq  "amma" && $simple eq "chechi") {
        $relation{'relation'} = "ammathe perassi";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }        
        return \%relation;
    }

    if ($custom eq  "amma" && $simple eq "aniyathi") {
        $relation{'relation'} = "chittashi";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }        
        return \%relation;
    }

    if ($custom eq  "amma" && $simple eq "ettan") {
        $relation{'relation'} = "valyammaman";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }        
        return \%relation;
    }

    if ($custom eq  "amma" && ($simple eq "aniyan" || $simple eq "brother")) {
        $relation{'relation'} = "ammaman";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }        
        return \%relation;
    }

    if(($custom eq "amma"        ||
        $custom eq "ammaman"     ||
        $custom eq "valyammaman" ||
        $custom eq "ammathe perassi" ) && $simple eq "achan") {
        $relation{'relation'} = "ammathe muthashan";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }        
        return \%relation;        
    }    

    if(($custom eq "amma"           ||
        $custom eq "ammaman"     ||
        $custom eq "valyammaman" ||
        $custom eq "ammathe perassi" ) && $simple eq "amma") {
        $relation{'relation'} = "ammathe muthashi";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }        
        return \%relation;
    }

    if(($custom eq "achan"        ||
        $custom eq "abhan"     ||
        $custom eq "valyachan" ||
        $custom eq "achammal"  ||
        $custom eq "illathe perassi" ) && $simple eq "achan") {

        $relation{'relation'} = "illathe muthashan";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;                        
    }

    if(($custom eq "achan"        ||
        $custom eq "abhan"     ||
        $custom eq "valyachan" ||
        $custom eq "achammal"  ||
        $custom eq "illathe perassi" ) && $simple eq "amma") {

        $relation{'relation'} = "illathe muthashi";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($custom_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $custom_ref->{'thavazhi'};
        }        
        return \%relation;        
    }

    if( $custom eq "ammathe muthashan" && ($simple eq "brother" || $simple eq "aniyan")) {
        $relation{'relation'} = "ammathe muthabhan";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;                                
    }

    if( $custom eq "ammathe muthashan" && $simple eq "ettan") {
        $relation{'relation'} = "ammathe valye muthashan";
        $relation{'record'} = $simple_ref->{'record'};
        return \%relation;                                
    }    

    if( ($custom eq "illathe muthashan" || $custom eq "illathe valye muthashan") && ($simple eq "brother" || $simple eq "aniyan"))  {  
        $relation{'relation'} = "illathe muthabhan";
        $relation{'record'} = $simple_ref->{'record'};
        if(exists($simple_ref->{'thavazhi'})) {
            $relation{'thavazhi'} = $simple_ref->{'thavazhi'};
            print prettyName($simple_ref->{'thavazhi'}->name)." thavazhi\n";
        }
        return \%relation;
    }

    if( $custom eq "illathe muthashan" && $simple eq "ettan")  {  
        $relation{'relation'} = "illathe valye muthashan";
        $relation{'record'} = $simple_ref->{'record'};                
        return \%relation;
    }

    $relation{'relation'} = "none";
    return \%relation;
}

