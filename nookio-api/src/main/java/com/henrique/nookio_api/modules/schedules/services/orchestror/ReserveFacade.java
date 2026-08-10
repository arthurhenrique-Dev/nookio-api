package com.henrique.nookio_api.modules.schedules.services.orchestror;

import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.modules.schedules.dto.CustomerDetailsInfo;
import com.henrique.nookio_api.modules.schedules.dto.PaymentRequestDto;
import com.henrique.nookio_api.modules.schedules.dto.ReserveScheduleDto;
import com.henrique.nookio_api.modules.schedules.models.Schedule;
import com.henrique.nookio_api.modules.schedules.services.reserve.PayReserveService;
import com.henrique.nookio_api.modules.schedules.services.reserve.ReserveScheduleService;
import com.henrique.nookio_api.modules.users.models.User;
import com.henrique.nookio_api.modules.users.repositories.UserRepository;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveFacade {

    private final UserRepository userRepository;
    private final ReserveScheduleService reserveSerice;
    private final PayReserveService paymentService;

    public void execute(ReserveScheduleDto dto) {
        String debugId = LogContext.getDebugId();
        Schedule schedule = reserveSerice.exec(dto);
        log.info("[RESERVE_FACADE_SCHEDULE_CREATED] debugId={} scheduleId={}", debugId, schedule.getId());

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.userId()));

        CustomerDetailsInfo customerDetailsInfo = CustomerDetailsInfo.builder()
                .fullname(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .taxId(user.getCpf())
                .phone(user.getPhoneNumber())
                .build();

        PaymentRequestDto request = new PaymentRequestDto(customerDetailsInfo, dto.paymentDto());
        paymentService.exec(request, schedule);
        log.info("[RESERVE_FACADE_PAYMENT_PROCESSED] debugId={} scheduleId={}", debugId, schedule.getId());
    }
}
