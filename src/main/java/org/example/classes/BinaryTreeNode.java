package org.example.classes;

public class BinaryTreeNode {

    private Integer data;
    private BinaryTreeNode left;
    private BinaryTreeNode right;

    public BinaryTreeNode(Integer data, BinaryTreeNode left, BinaryTreeNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    public Integer getData() {
        return data;
    }

    public BinaryTreeNode getLeft() {
        return left;
    }

    public BinaryTreeNode getRight() {
        return right;
    }

    public void setLeft(BinaryTreeNode left) {
        this.left = left;
    }

    public void setRight(BinaryTreeNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(sb, "", "", "");
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private void appendTo(StringBuilder sb, String linePrefix, String childPrefix, String label) {
        sb.append(linePrefix).append(label).append(data).append('\n');

        if (left != null) {
            boolean hasRight = right != null;
            left.appendTo(sb,
                    childPrefix + (hasRight ? "+--" : "\\--"),
                    childPrefix + (hasRight ? "|  " : "   "),
                    "L: ");
        }
        if (right != null) {
            right.appendTo(sb, childPrefix + "+--", childPrefix + "   ", "R: ");
        }
    }
}
