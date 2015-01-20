A = [0, 7, 6; 6, 0, 7; 7, 6, 0];
o = [1;1;1];
first = [A',-o;o',0];
rhs = [0;0;0;1];
disp(first \ rhs);
