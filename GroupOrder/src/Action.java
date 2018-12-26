public enum Action {
    ENTER_PILGRIM, ENTER_ROOM, CREATE_GROUP, SHOW_GROUP, SAVE_CHANGES, EXIT;

    @Override
    public String toString() {
        return super.toString() + " -> " + (this.ordinal() + 1);
    }
}
