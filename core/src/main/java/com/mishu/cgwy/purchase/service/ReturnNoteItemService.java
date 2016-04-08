package com.mishu.cgwy.purchase.service;

import com.mishu.cgwy.purchase.domain.*;
import com.mishu.cgwy.purchase.repository.ReturnNoteItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangguodong on 15/10/12.
 */
@Service
public class ReturnNoteItemService {

    @Autowired
    ReturnNoteItemRepository returnNoteItemRepository;

    public void save(ReturnNoteItem returnNoteItem) {
        returnNoteItemRepository.save(returnNoteItem);
    }
}
