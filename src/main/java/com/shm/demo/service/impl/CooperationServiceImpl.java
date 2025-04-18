package com.shm.demo.service.impl;

import com.shm.demo.dto.CooperationPersonnelRequest;
import com.shm.demo.dto.CreateCooperationRequest;
import com.shm.demo.dto.UpdateCooperationRequest; // 引入 Update DTO
import com.shm.demo.dto.PaginationRequest;
import com.shm.demo.dto.PageResponse;
import com.shm.demo.dto.CooperationListItemDTO;
import com.shm.demo.entity.*;
import com.shm.demo.mapper.CooperationMapper;
import com.shm.demo.mapper.CooperationPersonnelMapper;
import com.shm.demo.mapper.EnterpriseMapper;
import com.shm.demo.mapper.PersonnelMapper;
import com.shm.demo.service.CooperationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils; // 引入 CollectionUtils

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CooperationServiceImpl implements CooperationService {

    @Autowired
    private CooperationMapper cooperationMapper;

    @Autowired
    private CooperationPersonnelMapper cooperationPersonnelMapper;

    @Autowired
    private EnterpriseMapper enterpriseMapper; // 用于校验企业信息

    @Autowired
    private PersonnelMapper personnelMapper;   // 用于校验人员信息

    @Override
    @Transactional // 保证整个操作的原子性
    public Cooperation addCooperation(CreateCooperationRequest request) throws IllegalArgumentException {
        // 1. 基础校验 (DTO @Valid 已处理部分)
        validateCooperationDates(request.getCooperationStartDate(), request.getCooperationEndDate());
        validateCooperationThemeUnique(request.getCooperationTheme(), null); // 新增时 ID 为 null

        // 2. 校验人员列表及关联信息 
        validatePersonnelList(
                request.getInitiatorRegion(),
                request.getReceiverRegion(),
                request.getCooperationStartDate(),
                request.getCooperationEndDate(),
                request.getCooperationPersonnelList()
        );

        // 3. 校验人员时间重叠 (新增场景)
        checkForPersonnelTimeOverlap(request.getCooperationPersonnelList(), null); // 新增时排除 ID 为 null

        // 4. 创建 Cooperation 主记录
        Cooperation cooperation = new Cooperation();
        BeanUtils.copyProperties(request, cooperation);
        cooperation.setDeleted(0); // 确保是未删除状态
        cooperationMapper.insert(cooperation); // 获取自增 ID

        // 5. 准备并批量插入 CooperationPersonnel 明细记录
        List<CooperationPersonnel> personnelEntities = new ArrayList<>();
        for (CooperationPersonnelRequest personnelRequest : request.getCooperationPersonnelList()) {
            CooperationPersonnel personnelEntity = new CooperationPersonnel();
            BeanUtils.copyProperties(personnelRequest, personnelEntity);
            personnelEntity.setCooperationId(cooperation.getId()); // 关联主记录 ID
            personnelEntities.add(personnelEntity);
        }
        cooperationPersonnelMapper.batchInsert(personnelEntities);

        return cooperation; // 返回创建的主记录
    }


    // --- 新增 updateCooperation 方法 ---
    @Override
    @Transactional
    public Cooperation updateCooperation(UpdateCooperationRequest request) throws IllegalArgumentException {
        
              // 从 request DTO 中获取 ID
              Long id = request.getId();
              // DTO 上的 @NotNull 应该已经校验，但可以加一层保险
              if (id == null) {
                   throw new IllegalArgumentException("请求体中必须包含有效的合作ID");
              }
      
        // 1. 检查合作是否存在且未被删除
        Cooperation existingCooperation = cooperationMapper.findRawById(id);
        if (existingCooperation == null) {
            throw new IllegalArgumentException("未找到要更新的合作信息，ID: " + id);
        }
        if (existingCooperation.getDeleted() == 1) {
            throw new IllegalArgumentException("合作信息已被删除，无法更新，ID: " + id);
        }

        // 2. 基础校验
        validateCooperationDates(request.getCooperationStartDate(), request.getCooperationEndDate());
        validateCooperationThemeUnique(request.getCooperationTheme(), id); // 更新时传入 ID

        // 3. 校验人员列表及关联信息
        validatePersonnelList(
                request.getInitiatorRegion(),
                request.getReceiverRegion(),
                request.getCooperationStartDate(),
                request.getCooperationEndDate(),
                request.getCooperationPersonnelList()
        );

        // 4. 校验人员时间重叠 (更新场景，排除当前合作 ID)
        checkForPersonnelTimeOverlap(request.getCooperationPersonnelList(), id);

        // 5. 更新 Cooperation 主记录
        Cooperation cooperationToUpdate = new Cooperation();
        BeanUtils.copyProperties(request, cooperationToUpdate);
        cooperationToUpdate.setId(id); // 设置要更新的 ID
        // cooperationToUpdate.setDeleted(null); // 不应在此处设置 deleted
        int updatedRows = cooperationMapper.update(cooperationToUpdate);
        if (updatedRows == 0) {
             // 理论上前面已检查，但以防万一并发情况
             throw new IllegalStateException("更新合作信息时发生并发冲突或记录已被删除，ID: " + id);
        }


        // 6. 更新 CooperationPersonnel 明细记录 (先删后插策略)
        cooperationPersonnelMapper.deleteByCooperationId(id); // 删除旧的明细

        List<CooperationPersonnel> newPersonnelEntities = new ArrayList<>();
        for (CooperationPersonnelRequest personnelRequest : request.getCooperationPersonnelList()) {
            CooperationPersonnel personnelEntity = new CooperationPersonnel();
            BeanUtils.copyProperties(personnelRequest, personnelEntity);
            personnelEntity.setCooperationId(id); // 关联主记录 ID
            newPersonnelEntities.add(personnelEntity);
        }
        if (!newPersonnelEntities.isEmpty()) { // 确保列表不为空再插入
             cooperationPersonnelMapper.batchInsert(newPersonnelEntities);
        }


        // 7. 返回更新后的主记录信息
        return cooperationMapper.findById(id); // 重新查询以获取最新数据 (包括 updated_at)
    }


    // --- 私有校验方法 (可能需要调整以适应更新场景) ---

    private void validateCooperationDates(LocalDate startDate, LocalDate endDate) {
        // if (startDate.isAfter(LocalDate.now())) { // 更新时可能允许开始时间在未来
        //     throw new IllegalArgumentException("合作开始时间不能晚于当前日期");
        // }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("合作结束时间不能早于合作开始时间");
        }
    }


    // 调整 validateCooperationThemeUnique 以接受 ID
    private void validateCooperationThemeUnique(String theme, Long currentId) {
        int count;
        if (currentId == null) { // 新增场景
            count = cooperationMapper.countByTheme(theme);
        } else { // 更新场景
            count = cooperationMapper.countByThemeAndNotId(theme, currentId);
        }
        if (count > 0) {
            throw new IllegalArgumentException("合作主题已存在: " + theme);
        }
    }

    // 调整 validatePersonnelList 方法实现，使用传入的参数
    private void validatePersonnelList(String initiatorRegion, String receiverRegion, LocalDate mainStartDate, LocalDate mainEndDate, List<CooperationPersonnelRequest> personnelList) {
        // 移除错误的 request 引用，直接使用方法参数
        // LocalDate mainStartDate = request.getCooperationStartDate(); // 移除
        // LocalDate mainEndDate = request.getCooperationEndDate();   // 移除

        // 检查 personnelList 是否为空或 null (可选，取决于业务逻辑)
        if (CollectionUtils.isEmpty(personnelList)) {
             // 可以选择抛出异常或直接返回，取决于是否允许空的人员列表
             // throw new IllegalArgumentException("合作人员列表不能为空");
             return; // 如果允许空列表，则直接返回
        }


        for (CooperationPersonnelRequest personnelReq : personnelList) { // 使用传入的 personnelList
            // 校验人员日期
            validatePersonnelItemDates(personnelReq.getPersonnelStartDate(), personnelReq.getPersonnelEndDate(), mainStartDate, mainEndDate); // 使用传入的 mainStartDate, mainEndDate

            // 校验送出企业
            Enterprise sendingEnterprise = enterpriseMapper.findById(personnelReq.getSendingEnterpriseId());
            if (sendingEnterprise == null || sendingEnterprise.getDeleted() == 1) {
                throw new IllegalArgumentException("送出企业不存在或已被删除, ID: " + personnelReq.getSendingEnterpriseId());
            }
            if (!Objects.equals(sendingEnterprise.getRegion(), initiatorRegion)) { // 使用传入的 initiatorRegion
                throw new IllegalArgumentException("送出企业地区 '" + sendingEnterprise.getRegion() + "' 与发起方地区 '" + initiatorRegion + "' 不符, 企业ID: " + sendingEnterprise.getId());
            }
            if (sendingEnterprise.getCooperationType() != CooperationType.SEND) {
                throw new IllegalArgumentException("送出企业合作类型必须是 '送出(SEND)', 企业ID: " + sendingEnterprise.getId());
            }

            // 校验接收企业
            Enterprise receivingEnterprise = enterpriseMapper.findById(personnelReq.getReceivingEnterpriseId());
            if (receivingEnterprise == null || receivingEnterprise.getDeleted() == 1) {
                throw new IllegalArgumentException("接收企业不存在或已被删除, ID: " + personnelReq.getReceivingEnterpriseId());
            }
            if (!Objects.equals(receivingEnterprise.getRegion(), receiverRegion)) { // 使用传入的 receiverRegion
                throw new IllegalArgumentException("接收企业地区 '" + receivingEnterprise.getRegion() + "' 与接收方地区 '" + receiverRegion + "' 不符, 企业ID: " + receivingEnterprise.getId());
            }
            if (receivingEnterprise.getCooperationType() != CooperationType.RECEIVE) {
                throw new IllegalArgumentException("接收企业合作类型必须是 '接收(RECEIVE)', 企业ID: " + receivingEnterprise.getId());
            }

            // 校验人员是否存在且属于送出企业
            Personnel personnel = personnelMapper.findById(personnelReq.getPersonnelId());
            if (personnel == null || personnel.getDeleted() == 1) {
                throw new IllegalArgumentException("合作人员不存在或已被删除, ID: " + personnelReq.getPersonnelId());
            }
            if (!Objects.equals(personnel.getEnterpriseId(), sendingEnterprise.getId())) {
                throw new IllegalArgumentException("合作人员 (ID: " + personnel.getId() + ") 不属于指定的送出企业 (ID: " + sendingEnterprise.getId() + ")");
            }
        }
    }

    private void validatePersonnelItemDates(LocalDate personnelStartDate, LocalDate personnelEndDate, LocalDate mainStartDate, LocalDate mainEndDate) {
        if (personnelEndDate.isBefore(personnelStartDate)) {
            throw new IllegalArgumentException("人员合作结束时间不能早于其开始时间");
        }
        if (personnelStartDate.isBefore(mainStartDate) || personnelStartDate.isAfter(mainEndDate)) {
            throw new IllegalArgumentException("人员合作开始时间必须在合作主时间范围 [" + mainStartDate + " - " + mainEndDate + "] 内");
        }
        if (personnelEndDate.isBefore(mainStartDate) || personnelEndDate.isAfter(mainEndDate)) {
            throw new IllegalArgumentException("人员合作结束时间必须在合作主时间范围 [" + mainStartDate + " - " + mainEndDate + "] 内");
        }
    }


    // 调整 checkForPersonnelTimeOverlap 以接受要排除的 cooperationId
    private void checkForPersonnelTimeOverlap(List<CooperationPersonnelRequest> personnelList, Long excludedCooperationId) {
        // 1. 检查请求内部是否有同一个人时间重叠 (逻辑不变)
        Map<Long, List<CooperationPersonnelRequest>> personnelGroups = personnelList.stream()
                .collect(Collectors.groupingBy(CooperationPersonnelRequest::getPersonnelId));

        for (Map.Entry<Long, List<CooperationPersonnelRequest>> entry : personnelGroups.entrySet()) {
            List<CooperationPersonnelRequest> itemsForPerson = entry.getValue();
            if (itemsForPerson.size() > 1) {
                itemsForPerson.sort((a, b) -> a.getPersonnelStartDate().compareTo(b.getPersonnelStartDate()));
                for (int i = 0; i < itemsForPerson.size() - 1; i++) {
                    if (itemsForPerson.get(i + 1).getPersonnelStartDate().isBefore(itemsForPerson.get(i).getPersonnelEndDate()) ||
                        itemsForPerson.get(i + 1).getPersonnelStartDate().isEqual(itemsForPerson.get(i).getPersonnelEndDate())) {
                         throw new IllegalArgumentException("请求内部人员 (ID: " + entry.getKey() + ") 的合作时间存在重叠");
                    }
                }
            }
        }

        // 2. 检查请求中的每个人员是否与数据库中 *其他* 未删除合作时间重叠
        for (CooperationPersonnelRequest personnelReq : personnelList) {
            List<CooperationPersonnel> overlaps;
            if (excludedCooperationId == null) { // 新增场景
                overlaps = cooperationPersonnelMapper.findOverlappingPersonnelAssignments(
                        personnelReq.getPersonnelId(),
                        personnelReq.getPersonnelStartDate(),
                        personnelReq.getPersonnelEndDate()
                );
            } else { // 更新场景
                overlaps = cooperationPersonnelMapper.findOverlappingPersonnelAssignmentsExcludingCooperation(
                        personnelReq.getPersonnelId(),
                        personnelReq.getPersonnelStartDate(),
                        personnelReq.getPersonnelEndDate(),
                        excludedCooperationId // 传入要排除的 ID
                );
            }

            if (!CollectionUtils.isEmpty(overlaps)) {
                // 找到重叠，构造错误消息
                 CooperationPersonnel existing = overlaps.get(0);
                throw new IllegalArgumentException(
                        String.format("人员 (ID: %d) 在时间段 [%s - %s] 与现有合作 (ID: %d, 人员时段: [%s - %s]) 存在时间重叠",
                                personnelReq.getPersonnelId(),
                                personnelReq.getPersonnelStartDate(),
                                personnelReq.getPersonnelEndDate(),
                                existing.getCooperationId(),
                                existing.getPersonnelStartDate(),
                                existing.getPersonnelEndDate()
                        )
                );
            }
        }
    }

    // --- 新增 listCooperations 方法 ---
    @Override
    public PageResponse<CooperationListItemDTO> listCooperations(PaginationRequest paginationRequest) {
        int page = paginationRequest.getPage();
        int size = paginationRequest.getSize();
        // 计算数据库偏移量 (OFFSET 从 0 开始)
        int offset = (page - 1) * size;

        // 1. 查询总记录数
        long totalElements = cooperationMapper.countTotal();

        List<CooperationListItemDTO> content;
        if (totalElements == 0 || offset >= totalElements) {
            // 如果没有数据或请求的页码超出范围，返回空列表
            content = Collections.emptyList();
        } else {
            // 2. 查询当前页的数据 (Mapper 方法已包含人员数量计算和排序)
            content = cooperationMapper.findPaginatedWithCount(offset, size);
        }

        // 3. 计算总页数
        int totalPages = (int) Math.ceil((double) totalElements / size);
        if (totalPages == 0 && totalElements > 0) { // 至少有一页
             totalPages = 1;
        }


        // 4. 组装 PageResponse 对象
        return new PageResponse<>(content, page, size, totalElements, totalPages);
    }
}