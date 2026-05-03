const apiResponse = document.getElementById('api-response');

document.querySelectorAll('.tab-button').forEach(button => {
    button.addEventListener('click', () => {
        document.querySelectorAll('.tab-button').forEach(x => x.classList.remove('active'));
        document.querySelectorAll('.tab-panel').forEach(x => x.classList.remove('active'));
        button.classList.add('active');
        document.getElementById(`tab-${button.dataset.tab}`).classList.add('active');
    });
});

async function api(path, options = {}) {
    const response = await fetch(path, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });
    const text = await response.text();
    let body;
    try {
        body = text ? JSON.parse(text) : null;
    } catch {
        body = text;
    }
    apiResponse.textContent = JSON.stringify(body, null, 2);
    if (!response.ok) {
        throw new Error(body?.message || 'Request failed');
    }
    return body;
}

function populateSelect(select, items, valueKey, labelBuilder) {
    select.innerHTML = '';
    items.forEach(item => {
        const option = document.createElement('option');
        option.value = item[valueKey];
        option.textContent = labelBuilder(item);
        select.appendChild(option);
    });
}

async function loadReferenceData() {
    const [customers, facilities, products, bookings, rentals] = await Promise.all([
        api('/api/customers'),
        api('/api/facilities'),
        api('/api/products'),
        api('/api/service-bookings'),
        api('/api/rentals')
    ]);

    const servicePoints = facilities.filter(item => item.type === 'SERVICE_POINT');

    populateSelect(document.getElementById('customer-select'), customers, 'id', item => `${item.fullName} (${item.creditBalance})`);
    populateSelect(document.getElementById('service-point-select'), servicePoints, 'id', item => `${item.name} / ${item.city}`);
    populateSelect(document.getElementById('inventory-facility-select'), facilities, 'id', item => `${item.name} / ${item.city}`);
    populateSelect(document.getElementById('dispatch-source'), facilities, 'id', item => `${item.name} / ${item.city}`);
    populateSelect(document.getElementById('dispatch-target'), facilities, 'id', item => `${item.name} / ${item.city}`);
    populateSelect(document.getElementById('dispatch-product'), products, 'id', item => `${item.name} (${item.sku})`);
    populateSelect(document.getElementById('service-booking-select'), bookings, 'id', item => `${item.bookingNumber} / ${item.status}`);
    populateSelect(document.getElementById('service-intake-booking-select'), bookings, 'id', item => `${item.bookingNumber} / ${item.status}`);
    populateSelect(document.getElementById('service-action-booking-select'), bookings, 'id', item => `${item.bookingNumber} / ${item.status}`);
    populateSelect(document.getElementById('service-intake-product'), products, 'id', item => `${item.name} (${item.sku})`);
    populateSelect(document.getElementById('rental-select'), rentals, 'id', item => `${item.rentalNumber} / ${item.status}`);

    document.getElementById('reference-output').textContent = JSON.stringify({ customers, facilities, products }, null, 2);
    document.getElementById('service-bookings-output').textContent = JSON.stringify(bookings, null, 2);
    document.getElementById('dispatch-output').textContent = JSON.stringify(await api('/api/dispatch-requests'), null, 2);
    document.getElementById('rental-output').textContent = JSON.stringify(rentals, null, 2);
}

document.getElementById('refresh-reference-data').addEventListener('click', loadReferenceData);

document.getElementById('load-bikes-button').addEventListener('click', async () => {
    const city = document.getElementById('bike-city-select').value;
    const bikes = await api(`/api/bikes?city=${encodeURIComponent(city)}`);
    populateSelect(document.getElementById('bike-select'), bikes, 'id', item => `${item.modelName} / ${item.code} / ${item.pricePerMinute}`);
});

document.getElementById('load-overview-button').addEventListener('click', async () => {
    const facilityId = document.getElementById('inventory-facility-select').value;
    const result = await api(`/api/inventory/overview?facilityId=${facilityId}`);
    document.getElementById('inventory-output').textContent = JSON.stringify(result, null, 2);
});

document.getElementById('load-sales-button').addEventListener('click', async () => {
    const facilityId = document.getElementById('inventory-facility-select').value;
    const result = await api(`/api/inventory/sales-analysis?facilityId=${facilityId}&days=30`);
    document.getElementById('inventory-output').textContent = JSON.stringify(result, null, 2);
});

document.getElementById('service-booking-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    await api('/api/service-bookings', {
        method: 'POST',
        body: JSON.stringify({
            customerName: form.get('customerName'),
            customerEmail: form.get('customerEmail'),
            bikeBrand: form.get('bikeBrand'),
            bikeModel: form.get('bikeModel'),
            problemDescription: form.get('problemDescription'),
            preferredFrom: new Date(form.get('preferredFrom')).toISOString(),
            preferredTo: new Date(form.get('preferredTo')).toISOString(),
            servicePointId: form.get('servicePointId')
        })
    });
    await loadReferenceData();
});

document.getElementById('service-status-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    const estimatedCompletion = form.get('estimatedCompletionAt');
    await api(`/api/service-bookings/${form.get('bookingId')}/status`, {
        method: 'PATCH',
        body: JSON.stringify({
            status: form.get('status'),
            preliminaryPrice: form.get('preliminaryPrice') || null,
            estimatedCompletionAt: estimatedCompletion ? new Date(estimatedCompletion).toISOString() : null,
            notes: form.get('notes') || null
        })
    });
    await loadReferenceData();
});

document.getElementById('service-intake-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    const partQuantity = Number(form.get('partQuantity'));
    const requiredParts = partQuantity > 0
        ? [{
            productId: form.get('productId'),
            requestedQuantity: partQuantity,
            unitPrice: Number(form.get('partUnitPrice') || 0)
        }]
        : [];

    await api(`/api/service-bookings/${form.get('bookingId')}/repair-intake`, {
        method: 'POST',
        body: JSON.stringify({
            technicalState: form.get('technicalState'),
            additionalFindings: form.get('additionalFindings') || null,
            workItems: [{
                description: form.get('workDescription'),
                laborPrice: Number(form.get('laborPrice') || 0)
            }],
            requiredParts,
            clientApproved: form.get('clientApproved') === 'on'
        })
    });
    await loadReferenceData();
});

document.getElementById('complete-service-button').addEventListener('click', async () => {
    const bookingId = document.getElementById('service-action-booking-select').value;
    await api(`/api/service-bookings/${bookingId}/complete`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('no-show-service-button').addEventListener('click', async () => {
    const bookingId = document.getElementById('service-action-booking-select').value;
    await api(`/api/service-bookings/${bookingId}/no-show`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('reject-service-button').addEventListener('click', async () => {
    const bookingId = document.getElementById('service-action-booking-select').value;
    await api(`/api/service-bookings/${bookingId}/reject-estimate`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('load-service-history-button').addEventListener('click', async () => {
    const email = document.getElementById('service-history-email').value;
    const history = await api(`/api/service-bookings/history?customerEmail=${encodeURIComponent(email)}`);
    document.getElementById('service-history-output').textContent = JSON.stringify(history, null, 2);
});

document.getElementById('dispatch-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    await api('/api/dispatch-requests', {
        method: 'POST',
        body: JSON.stringify({
            sourceFacilityId: form.get('sourceFacilityId'),
            targetFacilityId: form.get('targetFacilityId'),
            priority: form.get('priority'),
            notes: form.get('notes'),
            items: [{
                productId: form.get('productId'),
                requestedQuantity: Number(form.get('requestedQuantity'))
            }]
        })
    });
    await loadReferenceData();
});

document.getElementById('pre-register-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    await api('/api/rentals/pre-register', {
        method: 'POST',
        body: JSON.stringify({
            customerId: form.get('customerId'),
            bikeId: form.get('bikeId'),
            plannedMinutes: Number(form.get('plannedMinutes'))
        })
    });
    await loadReferenceData();
});

document.getElementById('start-rental-button').addEventListener('click', async () => {
    const rentalId = document.getElementById('rental-select').value;
    await api(`/api/rentals/${rentalId}/start`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('finish-rental-button').addEventListener('click', async () => {
    const rentalId = document.getElementById('rental-select').value;
    await api(`/api/rentals/${rentalId}/finish`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('report-issue-button').addEventListener('click', async () => {
    const rentalId = document.getElementById('rental-select').value;
    await api(`/api/rentals/${rentalId}/issue`, {
        method: 'POST',
        body: JSON.stringify({
            issueType: document.getElementById('issue-type-select').value,
            description: document.getElementById('issue-description').value
        })
    });
    await loadReferenceData();
});

loadReferenceData().catch(error => {
    apiResponse.textContent = error.message;
});
