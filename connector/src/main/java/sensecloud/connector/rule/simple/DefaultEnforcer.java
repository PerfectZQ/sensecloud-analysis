package sensecloud.connector.rule.simple;

//@Slf4j
//public class DefaultEnforcer implements Enforcer<Connector, JSONObject, DefaultRule> {
//
//    private static final String ARR_REGEXP = "[^\\[\\]]+\\[\\]";
//
//    @Override
//    public JSONObject enforce(Connector connector, List<DefaultRule> rules) {
//        JSONObject result = new JSONObject();
//
//        for(DefaultRule rule : rules) {
//            JSONObject toHandle = null;
//            switch (rule.type()) {
//                case SOURCE:
//                    toHandle = connector.sourceConf();
//                    break;
//                case SINK:
//                    toHandle = connector.sinkConf();
//                    break;
//                default: log.warn("Unknown rule type : {}", rule.data());
//            }
//
//            if(toHandle == null) continue;
//
//            String[] nodeSequence = rule.data();
//            if(nodeSequence != null && nodeSequence.length > 0) {
//                JSON parent = result;
//
//                Object nd = null;
//                String nn = "";
//
//                for(int i = 0 ; i < nodeSequence.length ; i ++) {
//                    String node = nodeSequence[i];
//
//                    if (StringUtils.isNotBlank(node)) {
//                        if (node.matches(ARR_REGEXP)) {
//                            nd = new JSONArray();
//                            nn = node.substring(0, node.length() - 2);
//                        } else {
//                            nd = new JSONObject();
//                            nn = node;
//                        }
//
//                        if(i == nodeSequence.length - 1) {
//                            String jsonPath = rule.expression();
//                            nd = JSONPath.eval(toHandle, jsonPath);
//                        }
//
//                        if(parent instanceof JSONObject) {
//                            JSONObject _parent = (JSONObject) parent;
//                            _parent.put(nn, nd);
//                        } else {
//                            JSONArray _parent = (JSONArray) parent;
//                            _parent.add(nd);
//                        }
//
//                        if(i < nodeSequence.length - 1) parent = (JSON) nd;
//                    }
//                }
//            } else {
//                log.debug("Found no node sequence rule : {}", rule.toString());
//                continue;
//            }
//        }
//        log.debug("Generate result is : {}", result.toJSONString());
//        return result;
//    }
//
//    public static void main(String[] args) {
//        String t0 = "123[]";
//        String t1 = "12[]3";
//        String t2 = "[]123";
//        String t3 = "[]123[]";
//        System.out.println(t0 + ":" + t0.matches(ARR_REGEXP));
//        System.out.println(t1 + ":" + t1.matches(ARR_REGEXP));
//        System.out.println(t2 + ":" + t2.matches(ARR_REGEXP));
//        System.out.println(t3 + ":" + t3.matches(ARR_REGEXP));
//
//        System.out.println(t0.substring(0, t0.length() - 2));
//    }
//
//}
