package io.github.arcaneanviljeioptimization;
import java.util.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.MixinService;
final class ApiChecks {
 interface Nodes { ClassNode get(String name) throws Exception; }
 static final Nodes LIVE = name -> MixinService.getService().getBytecodeProvider().getClassNode(name.replace('/', '.'));
 static ClassNode node(Nodes nodes,String name) throws Exception { return nodes.get(name); }
 static boolean method(Nodes nodes,String owner,String name,String desc,int mask,int flags) throws Exception {
  return node(nodes,owner).methods.stream().anyMatch(m -> m.name.equals(name)&&m.desc.equals(desc)&&(m.access&mask)==flags);
 }
 static boolean field(Nodes nodes,String owner,String name,String desc,int mask,int flags) throws Exception {
  return node(nodes,owner).fields.stream().anyMatch(f -> f.name.equals(name)&&f.desc.equals(desc)&&(f.access&mask)==flags);
 }
 static boolean derives(Nodes nodes,String owner,String base) throws Exception {
  Set<String> seen=new HashSet<>();
  while(owner!=null&&seen.add(owner)) { if(owner.equals(base))return true;owner=node(nodes,owner).superName; }
  return false;
 }
}
