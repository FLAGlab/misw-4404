import math

class User:
    def __init__(self, t):
        self.t = t

def cPrice(ori,dest,packw, frag, usr):
    d = calc(ori,dest)
    c = cst(d,packw,frag)
    c = dsc(c,usr)
    return c


def calc(p1, p2):
    la1, lo1, la2, lo2 = map(math.radians, [p1[0], p1[1], p2[0], p2[1]])
    delta_la = la2 - la1
    delta_lo = lo2 - lo1
    a = math.sin(delta_la / 2) ** 2 + math.cos(la1) * math.cos(la2) * math.sin(delta_lo / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    R = 6371.0
    dis = R * c

    return dis

def cst(dis, packw, frag):
    if packw<=5:
        c = dis * 10
    else:
        c = dis * 10 + (packw - 5) * 2

    if frag:
        c = c * 1.5
    
    return c

def dsc(c, usr):
    if usr.t == 'gold':
        c = c * 0.8
    elif usr.t == 'silver':
        c = c * 0.9
    return c

def main():
    usr = User('gold')
    ori = (4.602, -74.065)
    dest = (40.748, -73.986)
    packw = 30
    frag = True
    c = cPrice(ori,dest,packw, frag, usr)
    print(c)

if __name__ == "__main__":
    main()
    