package org.ligi.passandroid.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class PassClassifier {

    public interface OnClassificationChangeListener {
        void OnClassificationChange();
    }

    public Set<OnClassificationChangeListener> onClassificationChangeListeners = new CopyOnWriteArraySet<>();

    public final static String DEFAULT_TOPIC = "active";

    private final HashMap<String, Set<String>> pass_id_list_by_topic;
    private final HashMap<String, String> topic_by_id = new HashMap<>();

    public PassClassifier(HashMap<String, Set<String>> pass_id_list_by_topic) {
        this.pass_id_list_by_topic = pass_id_list_by_topic;

        processDataChange();
    }

    private void processDataChange() {
        calculateReverseMapping();
        removeEmpty();
        makeSureDefaultTopicExists();

        for (OnClassificationChangeListener onClassificationChangeListener : onClassificationChangeListeners) {
            onClassificationChangeListener.OnClassificationChange();
        }
    }

    private void calculateReverseMapping() {
        topic_by_id.clear();
        for (Map.Entry<String, Set<String>> stringListEntry : pass_id_list_by_topic.entrySet()) {
            final String topic = stringListEntry.getKey();
            for (String id : stringListEntry.getValue()) {
                topic_by_id.put(id, topic);
            }
        }

    }

    private void makeSureDefaultTopicExists() {

        if (pass_id_list_by_topic.isEmpty()) {
            pass_id_list_by_topic.put(DEFAULT_TOPIC, new TreeSet<String>());
        }
    }

    private void removeEmpty() {
        final Set<String> toRemove = new HashSet<>();

        for (Map.Entry<String, Set<String>> stringListEntry : pass_id_list_by_topic.entrySet()) {
            if (stringListEntry.getValue().isEmpty()) {
                toRemove.add(stringListEntry.getKey());
            }
        }

        for (String s : toRemove) {
            pass_id_list_by_topic.remove(s);
        }
    }

    /*public PassClassifier(final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        deleted_passes = Arrays.asList(sharedPreferences.getString("deleted_passes", "").split(";;"));
        //deleted_passes
        //deleted_passes.co
    }*/

    public void moveToTopic(final Pass pass, final String newTopic) {
        if (topic_by_id.containsKey(pass.getId())) {
            final String oldTopic = topic_by_id.get(pass.getId());
            final Set<String> idsForOldTopic = pass_id_list_by_topic.get(oldTopic);
            idsForOldTopic.remove(pass.getId());
            if (idsForOldTopic.isEmpty()) {
                pass_id_list_by_topic.remove(oldTopic);
            }

        }

        upsertPassToTopic(pass, newTopic);

        processDataChange();
    }

    private void upsertPassToTopic(Pass pass, String newTopic) {
        if (!pass_id_list_by_topic.containsKey(newTopic)) {
            pass_id_list_by_topic.put(newTopic, new TreeSet<String>());
        }

        pass_id_list_by_topic.get(newTopic).add(pass.getId());
    }

    public Set<String> getTopics() {
        return pass_id_list_by_topic.keySet();
    }

    public boolean isPassOnTopic(FiledPass pass, String topic) {
        final boolean passIsClassified = topic_by_id.containsKey(pass.getId());

        if (passIsClassified) {
            return topic_by_id.get(pass.getId()).equals(topic);
        }

        if (topic.equals(DEFAULT_TOPIC)) {
            topic_by_id.put(pass.getId(), DEFAULT_TOPIC);
            if (pass_id_list_by_topic.get(DEFAULT_TOPIC) == null) {
                pass_id_list_by_topic.put(DEFAULT_TOPIC, new HashSet<String>());
            }
            final String id = pass.getId();
            final Set<String> strings = pass_id_list_by_topic.get(DEFAULT_TOPIC);
            if (id!=null) {
                strings.add(id);
            }
            return true;
        }

        return false;
    }
}
