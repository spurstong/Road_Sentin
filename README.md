# Road_Sentin  

Sentinel是一款优秀的进行流量控制、熔断降级、实时统计的中间件。  

在阅读有关源码之后，对Sentinel的有关功能进行了复现，去除了一些非核心的功能，只保留其核心功能。  

复现的功能主要有：  

- 处理链SlotChain的构建（责任链设计模式）
- 流量控制规则（FlowRule）和降级规则 (DegradeRule)的设计
- 实时数据统计节点 （DefaultNode）和（ClusterNode）的设计
- 秒级和分钟级的时间窗口设计
- 流量控制策略 
- 熔断降级策略 



