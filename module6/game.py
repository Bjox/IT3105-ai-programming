import random


class Game:
    def __init__(self):
        self.board = [0]*16
        return # why empty return? There are no damn brackets to mark end of function meaning readability = 0, stupid language

    def reset(self):
        self.board = [0]*16

    def get(self, r, c):
        return self.board[4*r+c]
    
    def set(self, r, c, val):
        self.board[4*r+c] = val

    def setboard(self, board):
        for r in range(4):
            for c in range(4):
                self.set(r, c, board[4*r+c])
        return

    def getboard(self):
        return self.board

    # move is int in range [0,3]
    # 0: up, 1: right, 2: down, 3: left
    def move(self, move):
        if   move == 0:
            return self.move_up()
        elif move == 1:
            return self.move_right()
        elif move == 2:
            return self.move_down()
        elif move == 3:
            return self.move_left()
        else:
            print("WTF", move)
        return False


    def move_up(self):
        tmpg = []
        for r in range(4):
            s = 0
            tmp = []
            for c in range(4):
                tmp.append(self.get(s, r))
                s += 1
            tmp = self.merge(tmp)
            tmpg.append(tmp)
        old_board = list(self.board)
        for r in range(4):
            for c in range(4):
                self.set(r, c, tmpg[c][r])
        return self.board != old_board


    def move_down(self):
        tmpg = []
        for r in range(4):
            s = 3
            tmp = []
            for c in range(4):
                tmp.append(self.get(s, r))
                s -= 1
            tmp = self.merge(tmp)
            tmpg.append(tmp)
        old_board = list(self.board)
        for r in range(4):
            for c in range(4):
                self.set(r, c, tmpg[c][3 - r])
        return self.board != old_board


    def move_left(self):
        tmpg = []
        for c in range(4):
            s = 0
            tmp = []
            for r in range(4):
                tmp.append(self.get(c, s))
                s += 1
            tmp = self.merge(tmp)
            tmpg.append(tmp)
        old_board = list(self.board)
        for r in range(4):
            for c in range(4):
                self.set(r, c, tmpg[r][c])
        return self.board != old_board


    def move_right(self):
        tmpg = []
        for c in range(4):
            s = 3
            tmp = []
            for r in range(4):
                tmp.append(self.get(c, s))
                s -= 1
            tmp = self.merge(tmp)
            tmpg.append(tmp)
        old_board = list(self.board)
        for r in range(4):
            for c in range(4):
                self.set(r, c, tmpg[r][3 - c])
        return self.board != old_board


    def merge(self, line):
        r = [0] * 4
        t = 0
        for i in range(4):
            if line[i] != 0:
                r[t] = line[i]
                t += 1
        for k in range(3):
            if r[k] != 0 and r[k] == r[k + 1]:
                r[k] += 1
                r.pop(k + 1)
                r.append(0)
        return r

    # returns true if a tile was spawned
    def spawn(self):
        free_squares = []
        for t in range(len(self.board)):
            if self.board[t] == 0: free_squares.append(t)
        if len(free_squares) == 0: return False
        self.board[free_squares[random.randint(0, len(free_squares)-1)]] = 1 if random.randint(0, 9) < 9 else 2
        return True

    # returns true if there are no legal moves for this board
    def isgameover(self):
        for move in range(0, 4):
            g = Game()
            g.setboard(list(self.board))
            if g.move(move): return False
        return True

    def newgame(self):
        self.setboard([0]*16)
        self.spawn()
        self.spawn()
        return

    def getMaxTile(self):
        return 2**(max(self.board))

    def printBoard(self):
        for r in range(4):
            print("[", end="")
            for c in range(4):
                print("{:2}".format(self.board[4*r+c]), end="")
                if c < 3:
                    print("|", end="")
            print("]")
