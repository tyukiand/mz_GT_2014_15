#include <stdio.h>

int lex(int a1, int a2, int b1, int b2) {
  return (a1 < b1) || (a1 == b1 && a2 <= b2);
}

int main(void) {
  int allImplicationsHold = 1;
  int x1; for (x1 = 0; x1 < 3; x1 ++) {
  int x2; for (x2 = 0; x2 < 3; x2 ++) {
  int y1; for (y1 = 0; y1 < 3; y1 ++) {
  int y2; for (y2 = 0; y2 < 3; y2 ++) {
  int z1; for (z1 = 0; z1 < 3; z1 ++) {
  int z2; for (z2 = 0; z2 < 3; z2 ++) {
    int xLy = lex(x1, x2, y1, y2);
    int yLz = lex(y1, y2, z1, z2);
    int xLz = lex(x1, x2, z1, z2);
    int implication = !(xLy && yLz) || xLz;
    allImplicationsHold = allImplicationsHold && implication;
    printf("%d %d %d %d\n", xLy, yLz, xLz, implication);
  }
  }
  }
  }
  }
  }
  printf("All implications hold: %d ", allImplicationsHold);
}
