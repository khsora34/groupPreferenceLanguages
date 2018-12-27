public enum Action {
    ENTER_PILGRIM, ENTER_GROUP, SHOW_AVAILABLE_PILGRIMS, SHOW_AVAILABLE_GROUP, MIX_GROUP, RELOAD_DATABASE, SAVE_CHANGES, EXIT;

    @Override
    public String toString() {
        return super.toString() + " -> " + (this.ordinal() + 1);
    }

    public static Action valueOf(int number) {
        Action res = null;
        Action[] actions = Action.values();

        for (int i = 0; res == null && i < actions.length; i++) {
            if (actions[i].ordinal() + 1 == number) {
                res = actions[i];
            }
        }
        return res;
    }
}
