#!/bin/bash

# 领域模型规则验证脚本
# 验证时间属性分离和代码规范

echo "========================================"
echo "领域模型时间属性分离验证"
echo "========================================"

# 1. 验证领域层没有时间属性
echo "1. 检查领域层是否包含时间属性..."
domain_files=$(find . -name "*.java" -path "*/domain/*" | wc -l)
echo "   领域层文件数量: $domain_files"

time_violations=$(find . -name "*.java" -path "*/domain/*" -exec grep -l "LocalDateTime\|java\.util\.Date\|Instant\|ZonedDateTime\|OffsetDateTime" {} \; | wc -l)
echo "   时间属性违规: $time_violations"

if [ $time_violations -eq 0 ]; then
    echo "   ✅ 领域层无时间属性违规"
else
    echo "   ❌ 发现时间属性违规文件:"
    find . -name "*.java" -path "*/domain/*" -exec grep -l "LocalDateTime\|java\.util\.Date\|Instant\|ZonedDateTime\|OffsetDateTime" {} \;
fi

# 2. 验证基础设施层保留时间属性
echo ""
echo "2. 检查基础设施层是否保留时间属性..."
infrastructure_files=$(find . -name "*.java" -path "*/infrastructure/*" -exec grep -l "createdAt\|updatedAt\|LocalDateTime\|java\.util\.Date" {} \; | wc -l)
echo "   基础设施层时间属性文件: $infrastructure_files"

if [ $infrastructure_files -gt 0 ]; then
    echo "   ✅ 基础设施层正确保留时间属性"
else
    echo "   ❌ 基础设施层缺少时间属性"
fi

# 3. 验证仓储实现正确性
echo ""
echo "3. 检查仓储实现的时间管理..."
repository_files=$(find . -name "*RepositoryImpl.java" -exec grep -l "Date now\|new Date()\|System.currentTimeMillis" {} \; | wc -l)
echo "   仓储实现时间管理文件: $repository_files"

if [ $repository_files -gt 0 ]; then
    echo "   ✅ 仓储实现包含时间管理逻辑"
else
    echo "   ❌ 仓储实现缺少时间管理"
fi

# 4. 验证领域对象结构
echo ""
echo "4. 验证领域对象结构..."

# 检查Appeal类
echo "   检查Appeal类..."
if grep -q "createdAt\|updatedAt" ai-master-domain/src/main/java/com/ai/master/appeal/domain/entity/Appeal.java; then
    echo "   ❌ Appeal类包含时间属性"
else
    echo "   ✅ Appeal类无时间属性"
fi

# 检查AppealItem类
echo "   检查AppealItem类..."
if grep -q "createdAt\|updatedAt" ai-master-domain/src/main/java/com/ai/master/appeal/domain/entity/AppealItem.java; then
    echo "   ❌ AppealItem类包含时间属性"
else
    echo "   ✅ AppealItem类无时间属性"
fi

# 检查Appealer类
echo "   检查Appealer类..."
if grep -q "createdAt\|updatedAt" ai-master-domain/src/main/java/com/ai/master/appeal/domain/entity/Appealer.java; then
    echo "   ❌ Appealer类包含时间属性"
else
    echo "   ✅ Appealer类无时间属性"
fi

# 5. 检查代码规范文件
echo ""
echo "5. 检查代码规范文件..."
if [ -f "checkstyle-domain-rules.xml" ]; then
    echo "   ✅ 领域规则检查文件存在"
else
    echo "   ❌ 缺少领域规则检查文件"
fi

if [ -f "checkstyle-suppressions.xml" ]; then
    echo "   ✅ 检查规则例外文件存在"
else
    echo "   ❌ 缺少检查规则例外文件"
fi

# 6. 总结
echo ""
echo "========================================"
echo "验证总结"
echo "========================================"

if [ $time_violations -eq 0 ] && [ $infrastructure_files -gt 0 ] && [ $repository_files -gt 0 ]; then
    echo "✅ 所有验证通过！领域模型时间属性分离完成"
    exit 0
else
    echo "❌ 验证失败，请检查上述问题"
    exit 1
fi