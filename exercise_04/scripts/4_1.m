U = [3,-3,0;2,6,4; 2,5,6];
o = [1;1;1];
first = [U',-o;o',0];
second = [-U, -o;o',0];
rhs = [0;0;0;1];
disp(first \ rhs);
disp(second \ rhs);
