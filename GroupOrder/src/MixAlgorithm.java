import java.util.*;

public class MixAlgorithm {

    private Map<Integer, Pilgrim> pilgrimsMap;
    private Map<Integer, Group> groupsMap;

    public MixAlgorithm(Map<Integer, Pilgrim> pilgrims, Map<Integer, Group> groups) {
        this.pilgrimsMap = pilgrims;
        this.groupsMap = groups;
    }

    public Map<Integer, Pilgrim> getPilgrimsMap() {
        return pilgrimsMap;
    }

    public Map<Integer, Group> getGroupsMap() {
        return groupsMap;
    }

    public void startAlgorithm(boolean useFilter, boolean useShuffle) {

        Pilgrim[] pilgrims = null;

        if (useShuffle) {
            pilgrims = shittyShuffle();
        } else {
            pilgrims = pilgrimsMap.values().toArray(new Pilgrim[0]);
        }

        if (useFilter) {
            pilgrims = Arrays.stream(pilgrims).filter(s -> s.getGroupId() == -1).toArray(Pilgrim[]::new);
        }

        for (Pilgrim actualPilgrim : pilgrims) {
            Group[] groups = groupsMap.values().toArray(new Group[0]);

            // If the pilgrim is a leader, we'll look for him a special place in a group.
            if (actualPilgrim.isLeader()) {
                // Look for groups that do not have leaders or are empty.

                boolean zeroConditionMet = false;
                int indexWithMinLeaders = -1;
                int minLeaders = Integer.MAX_VALUE;

                for (int i = 0; !zeroConditionMet && i < groups.length; i++) {
                    if (groups[i].getNumberOfParticipants() == 0 || groups[i].getNumberOfLeaders() == 0) {
                        indexWithMinLeaders = i;
                        minLeaders = 0;
                        zeroConditionMet = true;
                        continue;
                    } else if (groups[i].getNumberOfLeaders() < minLeaders && canTalkWithOtherLanguagesInGroup(groups[i], actualPilgrim.getOtherLanguages())) {
                        minLeaders = groups[i].getNumberOfLeaders();
                        indexWithMinLeaders = i;
                    }
                }

                if (indexWithMinLeaders != -1) {
                    addPilgrimToGroup(actualPilgrim, groups[indexWithMinLeaders]);
                    continue;
                }

                for (int i = 0; !zeroConditionMet && i < groups.length; i++) {
                    if (groups[i].getNumberOfParticipants() < minLeaders && canTalkWithNativeLanguageInGroup(groups[i], actualPilgrim.getNativeLanguage())) {
                        minLeaders = groups[i].getNumberOfParticipants();
                        indexWithMinLeaders = i;
                    }
                }

                if (indexWithMinLeaders != -1) {
                    addPilgrimToGroup(actualPilgrim, groups[indexWithMinLeaders]);
                    continue;
                }

                for (int i = 0; !zeroConditionMet && i < groups.length; i++) {
                    if (groups[i].getNumberOfParticipants() < minLeaders) {
                        minLeaders = groups[i].getNumberOfParticipants();
                        indexWithMinLeaders = i;
                    }
                }

                addPilgrimToGroup(actualPilgrim, groups[indexWithMinLeaders]);

            } else {
                boolean zeroConditionMet = false;
                int indexWithMinParticipants = -1;
                int minParticipants = Integer.MAX_VALUE;

                boolean needsMoreThinking = true;

                for (int i = 0; !zeroConditionMet && i < groups.length; i++) {
                    if (groups[i].getNumberOfParticipants() == 0) {
                        indexWithMinParticipants = i;
                        minParticipants = 0;
                        zeroConditionMet = true;
                        continue;
                    } else if (groups[i].getNumberOfParticipants() < minParticipants && canTalkWithOtherLanguagesInGroup(groups[i], actualPilgrim.getOtherLanguages())) {
                        minParticipants = groups[i].getNumberOfParticipants();
                        indexWithMinParticipants = i;
                    }
                }

                if (indexWithMinParticipants != -1) {
                    addPilgrimToGroup(actualPilgrim, groups[indexWithMinParticipants]);
                    continue;
                }

                for (int i = 0; !zeroConditionMet && i < groups.length; i++) {
                    if (groups[i].getNumberOfParticipants() < minParticipants && canTalkWithNativeLanguageInGroup(groups[i], actualPilgrim.getNativeLanguage())) {
                        minParticipants = groups[i].getNumberOfParticipants();
                        indexWithMinParticipants = i;
                    }
                }

                if (indexWithMinParticipants != -1) {
                    addPilgrimToGroup(actualPilgrim, groups[indexWithMinParticipants]);
                    continue;
                }

                for (int i = 0; !zeroConditionMet && i < groups.length; i++) {
                    if (groups[i].getNumberOfParticipants() < minParticipants) {
                        minParticipants = groups[i].getNumberOfParticipants();
                        indexWithMinParticipants = i;
                    }
                }

                addPilgrimToGroup(actualPilgrim, groups[indexWithMinParticipants]);

            }
        }
    }

    private Pilgrim[] shittyShuffle() {
        List<Pilgrim> list = Arrays.asList(pilgrimsMap.values().toArray(new Pilgrim[0]));
        Collections.shuffle(list, new Random(new Date().getTime()));
        return list.toArray(new Pilgrim[0]);
    }

    private void addPilgrimToGroup(Pilgrim pilgrim, Group group) {
        pilgrim.setGroupId(group.getId());
        pilgrimsMap.put(pilgrim.getId(), pilgrim);
        Set languages = group.getAllLanguages();
        languages.addAll(pilgrim.getOtherLanguages());
        languages.add(pilgrim.getNativeLanguage());
        group.setAllLanguages(languages);
        group.setNumberOfParticipants(group.getNumberOfParticipants() + 1);
        if (pilgrim.isLeader()) {
            group.setNumberOfLeaders(group.getNumberOfLeaders() + 1);
        }
        groupsMap.put(group.getId(), group);
    }


    private boolean canTalkWithOtherLanguagesInGroup(Group group, Set<Language> languages) {
        Set<Language> groupLanguages = group.getAllLanguages();

        if (groupLanguages == null || groupLanguages.isEmpty()) {
            return true;
        }

        boolean canTalkInTheGroup = false;
        Iterator it = groupLanguages.iterator();

        while (!canTalkInTheGroup && it.hasNext()) {
            canTalkInTheGroup = languages.contains(it.next());
        }
        return canTalkInTheGroup;
    }

    private boolean canTalkWithNativeLanguageInGroup(Group group, Language language) {
        Set<Language> spokenLanguages = group.getAllLanguages();

        if (spokenLanguages == null) {
            System.out.println("WTF");
            return false;
        }
        return spokenLanguages.contains(language);
    }


}
