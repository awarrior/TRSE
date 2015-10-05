#!/usr/bin/perl

# ----------------------------------------------------------------
# DTPgen.pl - generates a random Disjunctive Temporal Problem,
#             given a set of parameters k,n,m,L
# Author     : Massimo Idini
# Updated by : Claudio Castellini
# Date       : Summer 2003
#
# Use DTPgen <k> <n> <m> <L> <seed>, where
#   k     is the number of disjuncts per disjunction (forced to be 2)
#   n     is the number of variables
#   m     is the number of disjunctions
#   L     is the bound on the constant term (usually 100)
#   seed  is the random seed.
# ----------------------------------------------------------------

# parse command line switches

$k =$ARGV[0];
$n =$ARGV[1];
$m =$ARGV[2];
$L =$ARGV[3];
$seed =$ARGV[4];

# check their consistency

($k>=2)     || die "ERROR: only k >= 2 is admitted.\n" ;
($n>=3)     || die "ERROR: n must be greater than 2.\n";
($m>=3)     || die "ERROR: m must be greater than 2.\n";
($L>=0)     || die "ERROR: L cannot be negative.\n";
($#ARGV==4) || die "ERROR: use DTPgen <k> <n> <m> <L> <seed>\n";

# initialise random seed

srand($seed);

# stamp the problem with the problem's parameters

print STDOUT "#\n";
print STDOUT "# A random Disjunctive Temporal Problem\n";
print STDOUT "#\n";
print STDOUT "# k = $k literals per clause,\n";
print STDOUT "# n = $n arithmetic vars,\n";
print STDOUT "# m = $m clauses,\n";
print STDOUT "# L = 100 is the coefficients' range,\n";
print STDOUT "# random seed is $seed\n";
print STDOUT "#\n";

# generate m constraints:

for ( $i=0; $i<$m; $i++ ) {
   # place an AND after every clause but the last one
   if ( $i!=0 ) {
       print STDOUT " ^\n";
   }
   # now generate six random numbers a,b,c,d,e,f such that
   do {
   		$repeat=0;
   		for ( $j=0; $j<$k; $j++ ) {
       # the variable indexes (a,b) differ,
       while ( ($a=int(rand()*$n)) == ($b=int(rand()*$n)) ) { }
       # the constant term c is chosen randomly in [-L,L]
       $c = int(rand()*$L);
       $neg = rand();
       if ($neg>= 0.5) {
           $c=$c*-1;
       }
       $param[$j*3]= $a;
       $param[$j*3+1]= $b;
       $param[$j*3+2]= $c;
      }
      for ( $h=0; $h<=$k*3-6; $h=$h+3 ) {
       for ( $g= $h+3; $g<=$k*3-3; $g=$g+3 ){
         if( ($param[$h]==$param[$g]) && ($param[$h+1]==$param[$g+1]) && ($param[$h+2]==$param[$g+2])) {
           $repeat=1; 
           last if ($repeat==1);
          }
        }
      }
   }
   # finally check that we haven't generated the same relation twice!
   while ( $repeat==1 );

   # write otu the relations as a binary clause
   for($p=0; $p<=$k*3-3; $p=$p+3) {
   	if($p>0) { print STDOUT " v "; }
    print STDOUT "x$param[$p]-x$param[$p+1]<=$param[$p+2]";
  }
}

print STDOUT "\n";
