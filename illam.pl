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

if (@ARGV < 1) {
    print STDERR "Usage: perl illam.pl <name of database file>\n";
    exit 1;
}


#read gedcom file from user input  
my $ged = Gedcom->new(shift);
&main($ged);
my %userchoice;
my %visitedList;	#keeps track of whether a node was visited or not

sub main() {
  #&getSearches("Deepak");  
  

  system("clear");
  &printMainMenu();
}

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
			@results=&getSearches($id);
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

	@results=&getSearches($id);
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
=begin comment
sub familyMenu {
	

	print "Select person of interest: ";
	my $person = &selectPerson();
	
	print "Select invitee families of : \n";

	#children, grandchildren (descendants), 
	#siblings, 
	#parents, spouse parents, 
	#paternal siblings, maternal siblings, 
	#spouse paternal siblings, spouse maternal siblings
	#grand paternal siblings
	#grand maternal siblings

	print "Select :\n";
	my $choice = <STDIN>;

	my $child;
	my $grandchild;
	if($choice==1) {
		if(&hasChildren($person)) {
			foreach $child ($person->children) {
				print &prettyName($child->name)."\n";
			}
		}		
	}
	elsif($choice==2) {
		if(&hasChildren($person)) {
			foreach $child ($person->children) {
				if(&hasChildren($child)) {
					foreach $grandchild ($child->children) {
						print &prettyName($grandchild->name)."\n";
					}
				}
			}
		}		
	}
	elsif($choice==3) {
		if(&hasMother($person)) {
			foreach
		}	

	}
	else {
		print "Invalid\n";
	}
}
=end comment
=cut

sub getChildren {
	my @children;
	@children = $_->children;
	return @children;
}

sub getChildren {
	my @children;
	@children = $_->children;
	return @children;
}



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

sub pulaMenu {
	print "Calculate pula if the following person dies (Enter name): ";
	my $person = &selectPerson();
	print "Pula applies for \n";
	my @pulaList;
}



sub printDatabase {
  for my $i ($ged->individuals)
  {
	print &prettyName($i->name)."\n";
  }
  print "Database has ".&getTotalIndividuals()." members \n";
}

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

sub getTotalIndividuals {
	return $ged->individuals;
}


sub getSearches {
	my $name = shift;
	my @finds = &searchPerson($name);
	return @finds;	
}

sub searchPerson {
  my @matches = $ged->get_individual($_[0]);
  return @matches;
}

sub prettyName {
  my @names = split("/",$_[0]);
  my $given = $names[0];
  my $sur = $names[1];
  $given=~s/Unknown//g; #ignore unknown father's name
  return "$sur $given";
}

sub illam {
  my @names = split("/",$_[0]->name);
  my $sur = $names[1];
  return $sur;
}

sub ammath {
  if($_[0]->mother) {
  	return &illam($_[0]->mother);
  }
  else {
	return "Unknown\n";
  } 
}

sub hasFather {
	return ($_[0]->father)?1:0;
}

sub hasMother {
	return ($_[0]->mother)?1:0;
}

sub hasSpouse {
	return ($_[0]->spouse)?1:0;
}

sub hasSons {
	return ($_[0]->sons)?1:0;
}

sub hasDaughters {
	return ($_[0]->daughters)?1:0;
}

sub hasChildren {
	return (&hasSons($_[0])||&hasDaughters($_[0]));
}

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
		print "Spouse:\t\t[".$index++."] ".&prettyName($person->spouse->name)."\n";
		push(@relations,$person->spouse);
	}

	if(&hasSons($person)) {
		my @sons = $person->sons;
		print "Sons:";
		foreach (@sons) {
			print "\t\t[".$index++."] ".&prettyName($_->name)."\n";
			push(@relations,$_);

		}
	}

	if(&hasDaughters($person)) {
		my @daughters = $person->daughters;
		print "Daughters:";
		foreach (@daughters) {
			print "\t[".$index++."] ".&prettyName($_->name)."\n";
			push(@relations,$_);
		}
	}

	print "\n\nIllam:\t\t".&illam($person)."\n";
	if(&hasMother($person)) {
		print "Ammath:\t\t".&ammath($person)."\n";
	}

	if(&hasFather($person)) {	
		if(&hasMother($person->father)) {
			print "Achan's ammath:\t".&illam($person->father->mother)."\n";
		}
	}

	if(&hasMother($person)) {
		if(&hasMother($person->mother)) {
			print "Amma's ammath:\t".&illam($person->mother->mother)."\n";
		}
	}
#	print "Ancestors\n";
#	my @ancestors = $_[0]->ancestors;
#	foreach (@ancestors) {
#		print &prettyName($_->name)."\n";
#	}
	#&printRelation($person,$person->mother->father);
	
	print "\n*****************************************************\n";
	return @relations;
}


sub isVisited {
	if($visitedList{$_[0]} eq "v") {
		return 1;	
	}
	else {
		return 0;
	}
}


sub printRelation {
	my $personA = $_[0];
	my $personB = $_[1];

	#try to search in father, mother, all sons, all daughter, spouse's relations, mark visited
	my @searchQ;
	my %prevNodeList;	#keeps track of the way we first reached this node
	my $found=0;

	push(@searchQ,$personA);
	$prevNodeList{$personA}=$personA;
	
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
					if(!$prevNodeList{$n->father}) { $prevNodeList{$n->father}=$n; }
				}  
			}

			if(&hasMother($n)) { 	
				if(!(&isVisited($n->mother))) { 
					push(@searchQ,$n->mother); 
					#print "Adding ".$n->mother->name." prev is ".$n->name."\n";	
					if(!$prevNodeList{$n->mother}) { $prevNodeList{$n->mother}=$n; }
				}
			}

			if(&hasSpouse($n)) { 
				if(!(&isVisited($n->spouse))) { 
					push(@searchQ,$n->spouse); 
					#print "Adding ".$n->spouse->name." prev is ".$n->name."\n";	
					if(!$prevNodeList{$n->spouse}) { $prevNodeList{$n->spouse}=$n; }
				}
			}
	
			if(&hasSons($n)) {
				foreach($n->sons) {
					if(!(&isVisited($_))) { 
						push(@searchQ,$_);
						#print "Adding ".$_->name." prev is ".$n->name."\n";
						if(!$prevNodeList{$_}) { $prevNodeList{$_}=$n; }
					}
				}
			}
			if(&hasDaughters($n)) {
				foreach($n->daughters) {
					if(!(&isVisited($_))) { 
						push(@searchQ,$_);
						#print "Adding ".$_->name." prev is ".$n->name."\n";
						if(!$prevNodeList{$_}) { $prevNodeList{$_}=$n; }
					}
				}
			}
		}
	}
	
	my @relationStack;
	if($visitedList{$personB} eq "v") {
		#print "Match found\n";

		my $key;
		my $value;
		my $n = $personB;

		my @list;
		
		print"\nRelation found: ";
		#print "\t".$personB->name."\n";
		push(@list,$n); 
		while($n ne $personA) {
			#print "\t->".&prettyName($prevNodeList{$n}->name)."\n";
			$n=$prevNodeList{$n};
			push(@list,$n); 
		}

		my $prev = pop(@list);
		while(@list>0) {
			my $curr = pop(@list);
			#print &getRelation($prev,$curr)."'s ";

			if(@relationStack==0) {
				push(@relationStack,&getRelation($prev,$curr));
			}
			else {
				my $last = pop(@relationStack);
				if(&defineRelation($last,&getRelation($prev,$curr)) eq "none") {
					push(@relationStack,$last);
					push(@relationStack,&getRelation($prev,$curr));
				}
				else {
					push(@relationStack,&defineRelation($last,&getRelation($prev,$curr)));
				}
			}

			$prev = $curr;
		}
	}
	else {
		print "Match not found\n";
	}

	print &prettyName($personA->name)."'nde";
	while(@relationStack>0) {
		my $string = shift(@relationStack);
		if(@relationStack==0) {
			print " $string anu ".&prettyName($personB->name)."\n\n";
		}
		else {
			print " $string"."de";
		}
	}
}

#specify relation grammar here
sub getRelation {
	my $record_a = $_[0];
	my $record_b = $_[1];

	if(&hasFather($record_a)) {
		if($record_a->father==$record_b) { 
			return "achan"; 
		}
	}
	if(&hasMother($record_a)) {
		if($record_a->mother==$record_b) {
			return "amma"; 
		}
	}
	if(&hasSpouse($record_a)) { 
		if(($record_a->spouse==$record_b) && ($record_b->sex eq "F")) {
			return "wife";
		} 
		if(($record_a->spouse==$record_b) && ($record_b->sex eq "M")) {
			return "husband";
		}
	}
	if(&hasSons($record_a)) {
		foreach($record_a->sons) {
			if($_==$record_b) {
				return "makan";
			}
		}	
	}
	if(&hasDaughters($record_a)) {
		foreach($record_a->daughters) {
			if($_==$record_b) {
				return "makal";
			}
		}	
	}
	return "error";
}

sub defineRelation {
	my $personA = $_[0];
	my $personB = $_[1];

  	if ($personA eq  "achan" && $personB eq "wife") {
		 return "amma"; 
	}
  	if ($personA eq  "amma" && $personB eq "husband") {
		 return "achan"; 
	}
  	if ($personA eq  "achan" && $personB eq "makan") {
		 return "brother"; 
	}
  	if ($personA eq  "amma" && $personB eq "makan") {
		 return "brother"; 
	}
  	if ($personA eq  "achan" && $personB eq "makal") {
		 return "sister"; 
	}
  	if ($personA eq  "amma" && $personB eq "makal") {
		 return "sister"; 
	}
  	if ($personA eq  "amma" && $personB eq "achan") {
		 return "ammathe muthashan"; 
	}
  	if ($personA eq  "amma" && $personB eq "amma") {
		 return "ammathe muthashi"; 
	}
  	if ($personA eq  "achan" && $personB eq "achan") {
		 return "illathe muthashan"; 
	}
  	if ($personA eq  "achan" && $personB eq "amma") {
		 return "illathe muthashi"; 
	}
  	if ((($personA eq  "ammathe muthashan")||($personA eq "ammathe muthashi")) && $personB eq "makan") {
		 return "ammaman"; 
	}
  	if ((($personA eq  "ammathe muthashan")||($personA eq "ammathe muthashi")) && $personB eq "makal") {
		 return "perashi/shittashi"; 
	}
  	if ((($personA eq  "illathe muthashan")||($personA eq "illathe muthashi")) && $personB eq "makan") {
		 return "abhan/valyachan"; 
	}
  	if ((($personA eq  "illathe muthashan")||($personA eq "illathe muthashi")) && $personB eq "makal") {
		 return "achammal"; 
	}
  	if ($personA eq  "ammaman" && $personB eq "wife") {
		 return "ammayi"; 
	}
  	if ($personA eq  "ammayi" && $personB eq "husband") {
		 return "ammaman"; 
	}
  	if (($personA eq  "ammathe muthashan") && ($personB eq "brother")) {
		 return "ammathe muthabhan"; 
	}
  	if ($personA eq  "makal" && $personB eq "makal") {
		 return "perakutty"; 
	}
  	if ($personA eq  "makan" && $personB eq "makan") {
		 return "perakutty"; 
	}
	return "none";
}

