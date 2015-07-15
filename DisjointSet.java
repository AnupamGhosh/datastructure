import java.util.Arrays;

class DisjointSet {
	int setCount;
	int[] p;
	public DisjointSet(int n) {
		p = new int[n];
		Arrays.fill(p, -1);
		setCount = n;
	}
	void weightedUnion(int u, int v) {
		int uroot = compressedFind(u);
		int vroot = compressedFind(v);
		if (uroot == vroot) return;
		if (p[uroot] < p[vroot]) {
			p[uroot] += p[vroot];
			p[vroot] = uroot;
		}
		else {
			p[vroot] += p[uroot];
			p[uroot] = vroot;
		}
		setCount--;
	}
	int compressedFind(int v) {
		if (p[v] < 0) return v;
		return p[v] = compressedFind(p[v]);
	}
	int size(int v) {// size of set v
		return -p[compressedFind(v)];
	}
}
